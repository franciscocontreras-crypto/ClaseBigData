Integrante: Francisco
Integrante : Luis Lazcano

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

