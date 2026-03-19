val lista = List("rojo", "blanco" , "negro")
val lista: List[String] = List(rojo, blanco, negro)

scala> val lista2 = lista++List("verde", "amarillo", "azul","naranja", "
perla")
val lista2: List[String] = List(rojo, blanco, negro, verde, amarillo, azul, naranja, perla)

List(lista2(3), lista2(4), lista2(5))
val res4: List[String] = List(verde, amarillo, azul)

scala> val numeros = 1 to 1000 by 5
val numeros: scala.collection.immutable.Range = inexact Range 1 to 1000 by 5

scala> numeros.take(10)
val res5: scala.collection.immutable.Range = Range 1 to 46 by 5

// 5
val lista = List(1,3,3,4,6,7,3,7)

val unicos = lista.toSet

println(unicos)

//6
import scala.collection.mutable.Map

val nombres = Map(
  "Jose" -> 20,
  "Luis" -> 24,
  "Ana" -> 23,
  "Susana" -> 27
)

//7
println(nombres.keys)

//8
nombres += ("Miguel" -> 23)
println(nombres)
