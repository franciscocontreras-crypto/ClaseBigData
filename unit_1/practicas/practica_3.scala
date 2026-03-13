// Practica 3

// 1. Función para calcular el radio de un círculo a partir del área
def calcularRadio(area: Double): Double = {
  math.sqrt(area / math.Pi)
}

// 2. Función para verificar si un número es par
scala> def esPar(n: Int): Boolean = {
     | n % 2 == 0}
def esPar(n: Int): Boolean

scala> 8
val res5: Int = 8

scala> esPar(8)
val res6: Boolean = true


// 3. Ejercicios de strings
val bird = "tweet"
val frase = s"Estoy escribiendo un $bird"

val mensaje = "Hola Luke yo soy tu padre!"
val nombre = mensaje.slice(5,9)

// 4. Ejercicios de tuplas
val datos = (2,4,5,1,2,3,3.1416,23)
val datos: (Int, Int, Int, Int, Int, Int, Double, Int) = (2,4,5,1,2,3,3.1416,23)

scala> val pi = datos._7
val pi: Double = 3.1416