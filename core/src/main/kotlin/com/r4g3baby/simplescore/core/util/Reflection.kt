package com.r4g3baby.simplescore.core.util

import sun.misc.Unsafe
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup
import java.lang.invoke.MethodType
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object Reflection {
    private val lookup: Lookup = try {
        // get the unsafe class
        val unsafeClass = Class.forName("sun.misc.Unsafe")
        // get the unsafe instance
        val theUnsafe = unsafeClass.getDeclaredField("theUnsafe")
        theUnsafe.setAccessible(true)
        val unsafe = theUnsafe.get(null) as Unsafe
        // get the trusted lookup field
        val trustedLookup = Lookup::class.java.getDeclaredField("IMPL_LOOKUP")
        // get access to the base and offset value of it
        val offset = unsafe.staticFieldOffset(trustedLookup)
        val baseValue: Any = unsafe.staticFieldBase(trustedLookup)
        // get the trusted lookup instance
        unsafe.getObject(baseValue, offset) as Lookup
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        MethodHandles.lookup()
    }

    // static fields, converted as "public Object get()" and "public void set(Object value)"
    private val STATIC_FIELD_GETTER = MethodType.methodType(Any::class.java)
    private val STATIC_FIELD_SETTER = MethodType.methodType(Void.TYPE, Any::class.java)

    // instance fields, converted as "public Object get(Object instance)" and "public void set(Object instance, Object value)"
    private val VIRTUAL_FIELD_GETTER = MethodType.methodType(Any::class.java, Any::class.java)
    private val VIRTUAL_FIELD_SETTER = MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java)

    fun classExists(className: String): Boolean {
        try {
            Class.forName(className)
            return true
        } catch (_: ClassNotFoundException) {
            return false
        }
    }

    fun getClass(className: String): Class<*> {
        try {
            return Class.forName(className)
        } catch (ex: ClassNotFoundException) {
            throw IllegalArgumentException("Unable to find a class with the name $className", ex)
        }
    }

    fun findClass(vararg classAliases: String): Class<*> {
        for (classAlias in classAliases) {
            try {
                return getClass(classAlias)
            } catch (_: IllegalArgumentException) {
            }
        }

        throw IllegalArgumentException("Unable to find a class from the aliases ${classAliases.contentToString()}")
    }

    fun getConstructor(clazz: Class<*>, vararg parameters: Class<*>, filter: (Constructor<*>) -> Boolean = { true }): ConstructorInvoker {
        for (constructor in clazz.declaredConstructors.filter(filter)) {
            if (constructor.parameterTypes.contentEquals(parameters)) {
                val constructorHandle = convertToGeneric(lookup.unreflectConstructor(constructor), false, true)

                return ConstructorInvoker(constructor, constructorHandle)
            }
        }

        throw IllegalArgumentException("Unable to find a constructor with the parameters ${parameters.contentToString()} in $clazz")
    }

    fun getMethod(clazz: Class<*>, returnType: Class<*>, vararg parameters: Class<*>, filter: (Method) -> Boolean = { true }): MethodInvoker {
        for (method in clazz.declaredMethods.filter(filter)) {
            if (method.returnType == returnType && method.parameterTypes.contentEquals(parameters)) {
                val isStatic = Modifier.isStatic(method.modifiers)
                val methodHandle = convertToGeneric(lookup.unreflect(method), isStatic, false)

                return MethodInvoker(method, methodHandle)
            }
        }

        if (clazz.superclass != null) {
            return getMethod(clazz.superclass, returnType, *parameters)
        }

        throw IllegalArgumentException("Unable to find a method with the return type $returnType and parameters ${parameters.contentToString()} in $clazz")
    }

    fun getMethodByName(clazz: Class<*>, methodName: String, filter: (Method) -> Boolean = { true }): MethodInvoker {
        for (method in clazz.declaredMethods.filter(filter)) {
            if (method.name == methodName) {
                val isStatic = Modifier.isStatic(method.modifiers)
                val methodHandle = convertToGeneric(lookup.unreflect(method), isStatic, false)

                return MethodInvoker(method, methodHandle)
            }
        }

        if (clazz.superclass != null) {
            return getMethodByName(clazz.superclass, methodName)
        }

        throw IllegalArgumentException("Unable to find a method with the name $methodName in $clazz")
    }

    fun getField(clazz: Class<*>, fieldType: Class<*>, index: Int = 0, filter: (Field) -> Boolean = { true }): FieldAccessor {
        var currentIndex = index

        for (field in clazz.declaredFields.filter(filter)) {
            if (fieldType.isAssignableFrom(field.type) && currentIndex-- <= 0) {
                val isStatic = Modifier.isStatic(field.modifiers)

                val getter = if (isStatic) {
                    lookup.findStaticGetter(field.declaringClass, field.name, field.type).asType(STATIC_FIELD_GETTER)
                } else lookup.findGetter(field.declaringClass, field.name, field.type).asType(VIRTUAL_FIELD_GETTER)
                if (getter == null) throw IllegalStateException("Unable to access field $field. Could not find getter")

                val setter = if (isStatic) {
                    lookup.findStaticSetter(field.declaringClass, field.name, field.type).asType(STATIC_FIELD_SETTER)
                } else lookup.findSetter(field.declaringClass, field.name, field.type).asType(VIRTUAL_FIELD_SETTER)
                if (setter == null) throw IllegalStateException("Unable to access field $field. Could not find setter")

                return FieldAccessor(field, getter, setter, isStatic)
            }
        }

        if (clazz.superclass != null) {
            return getField(clazz.superclass, fieldType, currentIndex)
        }

        throw IllegalArgumentException("Unable to find a field with the type $fieldType in $clazz")
    }

    fun getArrayClass(componentType: Class<*>): Class<*> {
        val name = if (componentType.isArray) {
            "[${componentType.name}"
        } else when (componentType) {
            Boolean::class.javaPrimitiveType -> "[Z"
            Byte::class.javaPrimitiveType -> "[B"
            Char::class.javaPrimitiveType -> "[C"
            Double::class.javaPrimitiveType -> "[D"
            Float::class.javaPrimitiveType -> "[F"
            Int::class.javaPrimitiveType -> "[I"
            Long::class.javaPrimitiveType -> "[J"
            Short::class.javaPrimitiveType -> "[S"
            else -> "[L${componentType.name};"
        }
        return Class.forName(name, true, componentType.classLoader)
    }

    private fun convertToGeneric(handle: MethodHandle, isStatic: Boolean, ctor: Boolean): MethodHandle {
        var target = handle.asFixedArity()
        // special thing - we do not need the trailing array if we have 0 arguments anyway
        val paramCount = handle.type().parameterCount() - (if (ctor || isStatic) 0 else 1)
        val methodType = MethodType.genericMethodType(if (ctor) 0 else 1, true)
        // spread the arguments we give into the handle
        target = target.asSpreader(Array<Any>::class.java, paramCount)
        // adds a leading 'this' argument which we can ignore
        if (isStatic) {
            target = MethodHandles.dropArguments(target, 0, Any::class.java)
        }
        // convert the type to finish
        return target.asType(methodType)
    }

    class ConstructorInvoker(
        val constructor: Constructor<*>,
        private val constructorHandle: MethodHandle
    ) {
        fun invoke(vararg args: Any?): Any {
            return constructorHandle.invokeExact(args)
        }
    }

    class MethodInvoker(
        val method: Method,
        private val methodHandle: MethodHandle
    ) {
        fun invoke(instance: Any?, vararg args: Any?): Any? {
            return methodHandle.invoke(instance, args)
        }
    }

    class FieldAccessor(
        val field: Field,
        private val getter: MethodHandle,
        private val setter: MethodHandle,
        private val isStatic: Boolean
    ) {
        fun get(instance: Any?): Any? {
            return if (isStatic) getter.invokeExact() else getter.invokeExact(instance)
        }

        fun set(instance: Any?, value: Any?) {
            if (isStatic) setter.invokeExact(value) else setter.invokeExact(instance, value)
        }
    }
}