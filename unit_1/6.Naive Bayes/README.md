# Práctica: Naive Bayes Classifier con Apache Spark MLlib

## Descripción

En esta práctica se implementó un ejemplo de clasificación utilizando Apache Spark con Scala. El algoritmo utilizado fue **Naive Bayes**, específicamente mediante la librería clásica de Spark MLlib.

El objetivo principal fue cargar un conjunto de datos en formato LIBSVM, dividirlo en datos de entrenamiento y prueba, entrenar un modelo de clasificación Naive Bayes, evaluar su desempeño mediante la métrica de exactitud o `accuracy`, y finalmente guardar y cargar el modelo entrenado.

Este ejemplo permite comprender el flujo básico de un proceso de Machine Learning supervisado en Apache Spark utilizando MLlib.

## Objetivo de la práctica

El objetivo de esta práctica fue entrenar un modelo capaz de clasificar registros a partir de sus características numéricas.

Para ello se utilizó un archivo llamado:

```text
sample_data.txt
```

El archivo contiene datos en formato LIBSVM. En este formato, cada línea representa un registro del dataset. Al inicio de cada línea aparece la etiqueta o clase, y posteriormente se muestran las características con su índice y valor.

El modelo debe aprender a diferenciar entre las clases existentes en el archivo, en este caso clases representadas principalmente como:

```text
0
1
```

## Algoritmo utilizado

El algoritmo utilizado fue:

```scala
NaiveBayes
```

Este algoritmo pertenece a la librería:

```scala
org.apache.spark.mllib.classification
```

Naive Bayes es un algoritmo de clasificación supervisada basado en el Teorema de Bayes. Se utiliza comúnmente en problemas de clasificación donde se desea predecir la clase de un registro a partir de sus características.

Es muy utilizado en tareas como:

- Clasificación de textos.
- Clasificación de correos electrónicos.
- Detección de spam.
- Clasificación de documentos.
- Clasificación de imágenes o datos representados mediante vectores numéricos.

En esta práctica se utilizó el modelo de tipo:

```scala
multinomial
```

El modelo multinomial suele utilizarse cuando las características representan conteos, frecuencias o valores no negativos.

## Código ejecutado

```scala
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils

// Load and parse the data file.
val data = MLUtils.loadLibSVMFile(sc, "/Users/matinahernandez/Desktop/Proyectos/BigData/sample_data.txt")

// Split data into training (60%) and test (40%).
val Array(training, test) = data.randomSplit(Array(0.6, 0.4))

val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))

val accuracy = 1.0 * predictionAndLabel
  .filter(x => x._1 == x._2)
  .count() / test.count()

println("Accuracy = " + accuracy)

// Save and load model
model.save(sc, "target/tmp/myNaiveBayesModel")

val sameModel = NaiveBayesModel.load(sc, "target/tmp/myNaiveBayesModel")
```

## Explicación general del código

El código se divide en varias etapas principales:

1. Importación de librerías.
2. Carga del archivo en formato LIBSVM.
3. División del dataset en entrenamiento y prueba.
4. Entrenamiento del modelo Naive Bayes.
5. Generación de predicciones.
6. Cálculo de exactitud del modelo.
7. Guardado del modelo entrenado.
8. Carga del modelo guardado.

Cada una de estas etapas forma parte del flujo común de trabajo en un proceso de Machine Learning supervisado.

## Importación de librerías

Primero se importan las clases necesarias:

```scala
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils
```

La clase `NaiveBayes` permite entrenar el modelo de clasificación.

La clase `NaiveBayesModel` permite trabajar con el modelo generado, incluyendo la posibilidad de cargar un modelo previamente guardado.

La clase `MLUtils` permite cargar archivos en formatos utilizados comúnmente por Spark MLlib, como el formato LIBSVM.

## Carga del dataset

El archivo se carga mediante la siguiente instrucción:

```scala
val data = MLUtils.loadLibSVMFile(sc, "/Users/matinahernandez/Desktop/Proyectos/BigData/sample_data.txt")
```

Este código carga el archivo `sample_data.txt` desde una ruta local.

El archivo está en formato LIBSVM, donde cada línea tiene una estructura similar a la siguiente:

```text
<label> <indice1>:<valor1> <indice2>:<valor2> <indice3>:<valor3>
```

Por ejemplo:

```text
1 128:51 129:159 130:253
```

En este tipo de estructura:

- El primer valor representa la clase o etiqueta.
- Los valores posteriores representan características.
- Cada característica está formada por un índice y un valor.
- El índice indica la posición de la característica.
- El valor indica la magnitud o presencia de esa característica.

El archivo utilizado contiene registros con etiquetas como `0` y `1`, por lo que el modelo busca aprender a clasificar entre esas dos clases.

## Formato LIBSVM

El formato LIBSVM es muy utilizado en Machine Learning porque permite representar datos dispersos de forma eficiente.

Un dato disperso significa que muchas características tienen valor cero y no es necesario escribirlas explícitamente en el archivo.

Por ejemplo, en lugar de guardar un vector completo como:

```text
[0, 0, 0, 51, 0, 159, 0, 253]
```

LIBSVM guarda únicamente las posiciones que tienen valores diferentes de cero:

```text
4:51 6:159 8:253
```

Esto permite ahorrar espacio y trabajar con datasets de muchas características.

En el archivo utilizado se observan muchos valores con índices altos, lo cual indica que cada registro puede tener una gran cantidad de características posibles, aunque solo algunas aparecen con valores distintos de cero.

## División del dataset

Después de cargar los datos, el dataset se divide en dos partes:

```scala
val Array(training, test) = data.randomSplit(Array(0.6, 0.4))
```

Esto significa que:

| Conjunto | Porcentaje | Uso |
|---|---:|---|
| training | 60% | Entrenar el modelo |
| test | 40% | Evaluar el modelo |

El conjunto `training` se utiliza para que el algoritmo aprenda patrones a partir de los datos.

El conjunto `test` se utiliza para evaluar qué tan bien funciona el modelo con datos que no fueron usados directamente durante el entrenamiento.

Esta separación es importante porque permite medir si el modelo realmente aprendió patrones útiles y no solamente memorizó los datos de entrenamiento.

## Entrenamiento del modelo

El modelo se entrena con la siguiente instrucción:

```scala
val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")
```

Esta línea entrena un modelo Naive Bayes utilizando los datos de entrenamiento.

Los parámetros utilizados son:

| Parámetro | Descripción |
|---|---|
| training | Dataset usado para entrenar el modelo |
| lambda = 1.0 | Parámetro de suavizado |
| modelType = "multinomial" | Tipo de modelo Naive Bayes utilizado |

## Parámetro lambda

El parámetro `lambda` representa el suavizado o smoothing del modelo.

En esta práctica se utilizó:

```scala
lambda = 1.0
```

El suavizado ayuda a evitar problemas cuando una característica no aparece en una clase durante el entrenamiento.

Por ejemplo, si una característica aparece en registros de la clase `1`, pero nunca aparece en registros de la clase `0`, el modelo podría asignar una probabilidad cero. Esto puede afectar negativamente las predicciones.

El suavizado permite evitar probabilidades cero y mejora la estabilidad del modelo.

## Tipo de modelo multinomial

El modelo se configuró como:

```scala
modelType = "multinomial"
```

El modelo multinomial de Naive Bayes es adecuado cuando las características representan conteos, frecuencias o valores no negativos.

En este caso, el archivo `sample_data.txt` contiene valores numéricos no negativos asociados a diferentes índices de características, por lo que el modelo multinomial es una opción adecuada para esta práctica.

## Funcionamiento del algoritmo Naive Bayes

Naive Bayes se basa en el Teorema de Bayes, el cual permite calcular la probabilidad de que un registro pertenezca a una clase dada cierta evidencia.

De forma general, el algoritmo estima la probabilidad de cada clase y selecciona la clase con mayor probabilidad.

La idea puede representarse así:

```text
Clase predicha = clase con mayor probabilidad dadas sus características
```

El algoritmo analiza las características de cada registro y calcula qué tan probable es que pertenezca a cada clase posible.

Por ejemplo, si existen dos clases:

```text
0
1
```

El modelo calcula algo similar a:

```text
Probabilidad de que el registro pertenezca a la clase 0
Probabilidad de que el registro pertenezca a la clase 1
```

Después selecciona la clase con mayor probabilidad.

## Por qué se llama Naive Bayes

Se le llama `Naive` porque hace una suposición simplificada: considera que las características son independientes entre sí dentro de cada clase.

Esto significa que el algoritmo asume que cada característica aporta información de manera independiente.

Aunque esta suposición no siempre se cumple en datos reales, Naive Bayes puede funcionar muy bien en muchos problemas prácticos, especialmente cuando hay muchas características.

## Generación de predicciones

Después de entrenar el modelo, se generan predicciones sobre el conjunto de prueba:

```scala
val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
```

Esta línea recorre cada registro del conjunto `test`.

Para cada registro:

- `p.features` contiene las características del registro.
- `model.predict(p.features)` genera la predicción del modelo.
- `p.label` contiene la clase real.

El resultado es una colección de pares:

```text
(predicción, etiqueta_real)
```

Por ejemplo:

```text
(1.0, 1.0)
(0.0, 0.0)
(1.0, 0.0)
```

Si la predicción y la etiqueta real son iguales, el modelo clasificó correctamente ese registro.

Si son diferentes, el modelo cometió un error.

## Cálculo de accuracy

La exactitud del modelo se calcula con el siguiente código:

```scala
val accuracy = 1.0 * predictionAndLabel
  .filter(x => x._1 == x._2)
  .count() / test.count()
```

Esta instrucción realiza lo siguiente:

1. Compara cada predicción contra su etiqueta real.
2. Filtra únicamente las predicciones correctas.
3. Cuenta cuántas predicciones fueron correctas.
4. Divide ese número entre el total de registros del conjunto de prueba.

La fórmula general es:

```text
Accuracy = Predicciones correctas / Total de predicciones
```

Si el resultado es cercano a `1.0`, significa que el modelo tuvo un buen desempeño.

Si el resultado es cercano a `0.0`, significa que el modelo tuvo un bajo desempeño.

Por ejemplo:

```text
Accuracy = 0.85
```

significa que el modelo clasificó correctamente el 85% de los registros del conjunto de prueba.

## Resultado obtenido

El resultado se imprime con:

```scala
println("Accuracy = " + accuracy)
```

Un posible resultado puede ser:

```text
Accuracy = 0.8
```

Esto significa que el modelo clasificó correctamente el 80% de los registros del conjunto de prueba.

El valor exacto puede variar en cada ejecución porque la división de datos se realiza con:

```scala
data.randomSplit(Array(0.6, 0.4))
```

En este caso no se configuró una semilla fija, por lo tanto, cada ejecución puede generar una división diferente entre entrenamiento y prueba.

Como consecuencia, el valor de `accuracy` puede cambiar en diferentes ejecuciones.

## Interpretación del resultado

El resultado de `accuracy` permite saber qué proporción de registros fueron clasificados correctamente.

Por ejemplo, si el conjunto de prueba contiene 10 registros y el modelo clasifica correctamente 8, entonces:

```text
Accuracy = 8 / 10 = 0.8
```

Esto equivale a un 80% de exactitud.

Un valor alto de accuracy indica que el modelo logró aprender patrones útiles de los datos de entrenamiento.

Sin embargo, es importante considerar que la exactitud no siempre cuenta toda la historia del modelo. Si el dataset está desbalanceado, es decir, si una clase aparece mucho más que otra, el accuracy puede parecer alto aunque el modelo no esté clasificando bien todas las clases.

En esta práctica, el objetivo principal fue comprender el proceso de entrenamiento y evaluación del modelo, por lo que `accuracy` funciona como una métrica sencilla y adecuada para observar el desempeño general.

## Guardado del modelo

Después de entrenar el modelo, se guarda en una ruta local:

```scala
model.save(sc, "target/tmp/myNaiveBayesModel")
```

Esta instrucción guarda el modelo entrenado dentro de la carpeta:

```text
target/tmp/myNaiveBayesModel
```

Guardar el modelo es útil porque permite reutilizarlo posteriormente sin necesidad de volver a entrenarlo.

Esto es importante en proyectos reales, ya que el entrenamiento puede consumir tiempo y recursos.

## Carga del modelo

Después de guardar el modelo, se carga nuevamente con:

```scala
val sameModel = NaiveBayesModel.load(sc, "target/tmp/myNaiveBayesModel")
```

Esta instrucción recupera el modelo previamente guardado.

El modelo cargado puede utilizarse nuevamente para realizar predicciones sobre nuevos datos.

Esto demuestra el ciclo completo de trabajo de un modelo de Machine Learning:

1. Entrenar.
2. Evaluar.
3. Guardar.
4. Cargar.
5. Reutilizar.

## Observaciones de la práctica

Durante la práctica se observó que Spark MLlib permite cargar fácilmente datos en formato LIBSVM mediante `MLUtils.loadLibSVMFile`.

También se observó que el algoritmo Naive Bayes requiere que las características sean valores no negativos, especialmente cuando se utiliza el modelo de tipo `multinomial`.

El archivo `sample_data.txt` contiene datos con etiquetas y múltiples características representadas por índices y valores. Esto indica que se está trabajando con vectores dispersos, lo cual es común en problemas con muchas variables.

Se observó además que el algoritmo Naive Bayes puede entrenarse con pocas líneas de código, lo cual facilita su uso para prácticas iniciales de clasificación.

Una observación importante es que la división del dataset no utiliza una semilla fija. Por esta razón, el resultado del accuracy puede variar cada vez que se ejecuta el programa.

Para obtener resultados reproducibles, se podría modificar la división de datos de la siguiente forma:

```scala
val Array(training, test) = data.randomSplit(Array(0.6, 0.4), seed = 12345)
```

De esta manera, Spark dividiría los datos de la misma forma en cada ejecución.

## Posibles errores encontrados

Un posible error puede ocurrir si la ruta del archivo no es correcta:

```scala
"/Users/matinahernandez/Desktop/Proyectos/BigData/sample_data.txt"
```

Si el archivo no existe en esa ubicación, Spark mostrará un error indicando que no puede encontrar el archivo.

En ese caso, se debe verificar que el archivo `sample_data.txt` esté guardado correctamente en la ruta indicada.

Si se ejecuta en Windows, la ruta debería cambiarse a un formato como:

```scala
"C:/Spark/data/sample_data.txt"
```

Otro posible error puede ocurrir si el archivo no está correctamente estructurado en formato LIBSVM. Cada línea debe iniciar con una etiqueta y después contener características en formato índice:valor.

También puede ocurrir un error al guardar el modelo si la carpeta ya existe:

```scala
target/tmp/myNaiveBayesModel
```

Spark normalmente no sobrescribe una carpeta existente al guardar un modelo. Si la carpeta ya existe, se debe eliminar antes de volver a ejecutar el código, o guardar el modelo con otro nombre.

Por ejemplo:

```scala
model.save(sc, "target/tmp/myNaiveBayesModel2")
```

También es importante considerar que este código utiliza la librería antigua `mllib`, la cual trabaja con RDDs. Spark también cuenta con una API más nueva llamada `ml`, que trabaja principalmente con DataFrames.

## Diferencia entre MLlib y Spark ML

En esta práctica se utilizó la librería:

```scala
org.apache.spark.mllib
```

Esta es la API original de Machine Learning en Spark y está basada principalmente en RDDs.

Spark también tiene una API más reciente:

```scala
org.apache.spark.ml
```

La API `spark.ml` trabaja con DataFrames y Pipelines, por lo que suele ser más recomendada para proyectos modernos.

Sin embargo, `mllib` sigue siendo útil para comprender los fundamentos de Machine Learning en Spark y para ejecutar ejemplos clásicos de la documentación.

## Funcionamiento general del flujo de Machine Learning

El flujo completo realizado en esta práctica puede resumirse de la siguiente manera:

1. Se carga el archivo `sample_data.txt`.
2. Spark interpreta los datos en formato LIBSVM.
3. Cada registro se convierte en un punto etiquetado con características.
4. El dataset se divide en entrenamiento y prueba.
5. Naive Bayes aprende la relación entre características y etiquetas.
6. El modelo realiza predicciones sobre datos de prueba.
7. Se comparan las predicciones contra las etiquetas reales.
8. Se calcula la exactitud del modelo.
9. El modelo se guarda en disco.
10. El modelo se carga nuevamente para demostrar su reutilización.

## Conclusión

Se logró implementar correctamente un modelo de clasificación utilizando `NaiveBayes` en Apache Spark con Scala.

La práctica permitió comprender cómo cargar datos en formato LIBSVM, dividirlos en conjuntos de entrenamiento y prueba, entrenar un modelo Naive Bayes, generar predicciones, calcular la exactitud del modelo y guardar/cargar el modelo entrenado.

El algoritmo Naive Bayes es una técnica sencilla pero poderosa para problemas de clasificación. Su funcionamiento se basa en calcular probabilidades para determinar la clase más probable de cada registro.

En este ejemplo, el modelo fue entrenado con datos representados mediante características dispersas, lo cual demuestra la utilidad del formato LIBSVM para manejar datasets con muchas variables.

Además, se observó que el uso de `accuracy` permite evaluar de forma simple el desempeño general del modelo, aunque en problemas reales también pueden utilizarse otras métricas como precisión, recall o F1-score.

Esta práctica demuestra que Apache Spark MLlib puede utilizarse de forma eficiente para construir, evaluar y reutilizar modelos de Machine Learning en entornos de procesamiento distribuido.