* Integrante: Francisco
* Integrante : Luis Lazcano

# Práctica 2
```scala 
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
```

```scala
// 5
val lista = List(1,3,3,4,6,7,3,7)

val unicos = lista.toSet

println(unicos)
``` 

```scala
//6
import scala.collection.mutable.Map

val nombres = Map(
  "Jose" -> 20,
  "Luis" -> 24,
  "Ana" -> 23,
  "Susana" -> 27
)
```
```scala
//7
println(nombres.keys)

//8
nombres += ("Miguel" -> 23)
println(nombres)
```
# Práctica 3


# Función para calcular el radio de un círculo a partir del área
```scala
def calcularRadio(area: Double): Double = {
  math.sqrt(area / math.Pi)
}
```

# Función para verificar si un número es par

```scala
scala> def esPar(n: Int): Boolean = {
     | n % 2 == 0}
def esPar(n: Int): Boolean

scala> 8
val res5: Int = 8

scala> esPar(8)
val res6: Boolean = true
```

# Ejercicios de strings
```scala
val bird = "tweet"
val frase = s"Estoy escribiendo un $bird"

val mensaje = "Hola Luke yo soy tu padre!"
val nombre = mensaje.slice(5,9)
```
# Ejercicios de tuplas 
```scala
val datos = (2,4,5,1,2,3,3.1416,23)
val datos: (Int, Int, Int, Int, Int, Int, Double, Int) = (2,4,5,1,2,3,3.1416,23)

scala> val pi = datos._7
val pi: Double = 3.1416
```

# Sesión 6 – Análisis de Código en Scala

## Objetivo

El objetivo de esta actividad es analizar y explicar el funcionamiento del código desarrollado en la sesión 6.  
Se busca entender qué hace cada método, cómo procesa los datos que recibe y cuál es el resultado que produce cuando se ejecuta con los ejemplos del programa.

---

# Análisis del Código

El programa contiene varios métodos que trabajan con listas de números y con cadenas de texto.  
Cada método resuelve un problema diferente utilizando estructuras básicas de Scala como ciclos `for`, condicionales `if`, operaciones matemáticas y funciones de listas.

---

# Método `listEvens`

```scala
def listEvens(list:List[Int]): String ={
    for(n <- list){
        if(n%2==0){
            println(s"$n is even")
        }else{
            println(s"$n is odd")
        }
    }
    return "Done"
}
```
# Explicacion
---
Este método recorre una lista de números y verifica si cada número es par o impar.

Primero recibe una lista de enteros. Después usa un ciclo for para recorrer todos los elementos de la lista.
En cada iteración revisa si el número dividido entre 2 tiene residuo 0 (n % 2 == 0).

* Si se cumple la condición, el número es par.
* Si no se cumple, el número es impar.

El método imprime el resultado en pantalla para cada número y al final devuelve el texto "Done".

#Ejemplo

```scala
val l = List(1,2,3,4,5,6,7,8)
listEvens(l)
```
# Resultado
```bash
1 is odd
2 is even
3 is odd
4 is even
5 is odd
6 is even
7 is odd
8 is even

```
# Metodo afortunado

```scala
def afortunado(list:List[Int]): Int={
    var res=0
    for(n <- list){
        if(n==7){
            res = res + 14
        }else{
            res = res + n
        }
    }
    return res
}

```

# Explicacion
Este método calcula la suma de todos los números de una lista, pero tiene una regla especial.
Cuando encuentra el número 7, en lugar de sumar 7 suma 14.

Para lograrlo se usa una variable acumuladora llamada res que inicia en 0.
Después se recorre la lista con un for.

* Si el número es 7, se agregan 14 al resultado.

* Si no es 7, se suma el valor normal.

Al final el método devuelve el total acumulado.


# Resumen

En esta práctica se trabajó con varios métodos que permiten entender cómo procesar datos en Scala utilizando listas, ciclos y condiciones.

Cada función tiene un propósito diferente:

* listEvens identifica números pares e impares dentro de una lista.

* afortunado suma los números de una lista aplicando una regla especial cuando aparece el número 7.

* balance verifica si una lista puede dividirse en dos partes con la misma suma.

* palindromo revisa si una palabra se lee igual al derecho y al revés.
<<<<<<< HEAD

=======
>>>>>>> 460e8142930f960c25d007afe6275bc7f3fbeb9e
