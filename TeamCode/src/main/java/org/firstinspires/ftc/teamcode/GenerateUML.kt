package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.wheelmanager.WheelManager

//https://www.planttext.com/
object GenerateUML {
    val traversed = HashSet<Class<*>>()

    @JvmStatic
    public fun main(args : Array<String>) {
        println("@startuml")
        printInfo(ManualBW::class.java)
        println("@enduml")
    }

    fun <T> printInfo(clazz: Class<T>) {
        val clazzName = clazz.simpleName
        val packName = clazz.`package`?.name ?: ""
        val sup = clazz.superclass
        val supName = sup?.simpleName
        val methods = clazz.declaredMethods
        val fields = clazz.declaredFields

        if (!packName.matches(Regex("org.firstinspires.*|com.qualcomm.robotcore.*")) or
                traversed.contains(clazz)) return

        traversed.add(clazz)
        println("'$packName")
        supName?.let { println("$it <|-- $clazzName") }
        fields.forEach { field ->
            val fieldName = field.name
            val fieldType = field.type
            println("$clazzName : ${fieldType.simpleName} $fieldName")
            fieldType?.let { printInfo(it) }
        }
        methods.forEach { method ->
            val methodName = method.name
            val parTypes = method.parameterTypes.asList().map { it.simpleName }.let {
                if (it.isNotEmpty()) it.reduceRight { str, acc -> "$acc, $str"}
                else ""
            }
            val retType = method.returnType.simpleName.let { if (it == "void") "" else "$it " }

            println("$clazzName : $retType$methodName($parTypes)")
        }
    }
}