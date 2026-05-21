# Práctica: Multilayer Perceptron Classifier con Apache Spark

## Descripción

En esta práctica se ejecutó un ejemplo de la documentación de Apache Spark correspondiente al algoritmo **Multilayer Perceptron Classifier** utilizando el lenguaje Scala.

El objetivo principal fue cargar un conjunto de datos en formato LIBSVM, dividirlo en datos de entrenamiento y prueba, configurar una red neuronal multicapa, entrenar el modelo y evaluar su desempeño mediante la métrica de exactitud, conocida como `accuracy`.

Este tipo de algoritmo forma parte del área de Machine Learning y se utiliza principalmente para problemas de clasificación, donde el modelo aprende a identificar a qué clase pertenece cada registro de acuerdo con sus características.

## Algoritmo utilizado

El algoritmo utilizado fue:

```scala
MultilayerPerceptronClassifier
```

Este algoritmo pertenece a la librería:

```scala
org.apache.spark.ml.classification
```

El **Multilayer Perceptron Classifier** es un modelo de red neuronal artificial. Está formado por varias capas de neuronas conectadas entre sí. Cada capa recibe información, realiza cálculos internos y envía el resultado a la siguiente capa.

Una red neuronal multicapa normalmente está compuesta por:

- Una capa de entrada.
- Una o más capas ocultas.
- Una capa de salida.

La capa de entrada recibe las características del dataset. Las capas ocultas procesan la información mediante pesos y funciones de activación. Finalmente, la capa de salida genera la predicción correspondiente a una clase.

En esta práctica, el algoritmo se utilizó para resolver un problema de clasificación multiclase, es decir, un problema donde existen más de dos posibles categorías o clases.

## Dataset utilizado

Se utilizó el archivo:

```text
sample_multiclass_classification_data.txt
```

La ruta utilizada para cargar el archivo fue:

```text
C:/Spark/data/mllib/sample_multiclass_classification_data.txt
```

El archivo está en formato **LIBSVM**, que es un formato comúnmente utilizado para representar datos en problemas de Machine Learning. En este formato, cada registro contiene una etiqueta o clase y un conjunto de características numéricas.

Spark puede leer este tipo de archivos utilizando:

```scala
spark.read.format("libsvm")
```

## Código ejecutado

```scala
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

// Load the data stored in LIBSVM format as a DataFrame.
val data = spark.read
  .format("libsvm")
  .load("C:/Spark/data/mllib/sample_multiclass_classification_data.txt")

val Array(training, test) = data.randomSplit(Array(0.6, 0.4), seed = 12345)

// specify layers for the neural network:
// input layer of size 4 (features), two intermediate of size 5 and 4
// and output of size 3 (classes).
val layers = Array[Int](4, 5, 4, 3)

// create the trainer and set its parameters
val trainer = new MultilayerPerceptronClassifier()
  .setLayers(layers)
  .setBlockSize(128)
  .setSeed(1234L)
  .setMaxIter(100)

// train the model
val model = trainer.fit(training)

// compute accuracy on the test set
val result = model.transform(test)
val predictionAndLabels = result.select("prediction", "label")

val evaluator = new MulticlassClassificationEvaluator()
  .setMetricName("accuracy")

println(s"Test set accuracy = ${evaluator.evaluate(predictionAndLabels)}")
```

## Explicación del código

Primero se importan las librerías necesarias para crear el modelo y evaluarlo:

```scala
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
```

La primera importación permite utilizar el clasificador de red neuronal multicapa. La segunda importación permite evaluar el resultado del modelo mediante métricas de clasificación.

Después se carga el dataset en formato LIBSVM:

```scala
val data = spark.read
  .format("libsvm")
  .load("C:/Spark/data/mllib/sample_multiclass_classification_data.txt")
```

Spark lee el archivo y lo convierte en un DataFrame. Este DataFrame contiene principalmente dos columnas importantes:

| Columna | Descripción |
|---|---|
| label | Representa la clase real de cada registro |
| features | Representa las características utilizadas para entrenar el modelo |

Luego se divide el dataset en dos partes:

```scala
val Array(training, test) = data.randomSplit(Array(0.6, 0.4), seed = 12345)
```

El 60% de los datos se utiliza para entrenamiento y el 40% restante se utiliza para prueba.

| Conjunto | Porcentaje | Uso |
|---|---:|---|
| training | 60% | Entrenar el modelo |
| test | 40% | Evaluar el modelo |

El parámetro `seed = 12345` permite que la división de los datos sea reproducible. Esto significa que, al ejecutar nuevamente el código con la misma semilla, Spark realizará la misma separación de datos.

## Configuración de la red neuronal

La estructura de la red neuronal se define con el siguiente arreglo:

```scala
val layers = Array[Int](4, 5, 4, 3)
```

Este arreglo indica la cantidad de neuronas que tendrá cada capa de la red neuronal.

| Capa | Número de neuronas | Descripción |
|---|---:|---|
| Entrada | 4 | Corresponde al número de características del dataset |
| Oculta 1 | 5 | Primera capa intermedia de procesamiento |
| Oculta 2 | 4 | Segunda capa intermedia de procesamiento |
| Salida | 3 | Corresponde al número de clases posibles |

La capa de entrada tiene 4 neuronas porque el dataset contiene 4 características. La capa de salida tiene 3 neuronas porque el problema es de clasificación multiclase con 3 clases posibles.

Las capas ocultas permiten que el modelo aprenda relaciones más complejas entre las características de entrada y la clase de salida.

## Creación del entrenador

El modelo se configura mediante el siguiente código:

```scala
val trainer = new MultilayerPerceptronClassifier()
  .setLayers(layers)
  .setBlockSize(128)
  .setSeed(1234L)
  .setMaxIter(100)
```

Cada parámetro tiene una función específica:

| Parámetro | Descripción |
|---|---|
| setLayers(layers) | Define la arquitectura de la red neuronal |
| setBlockSize(128) | Define el tamaño de bloque usado para el entrenamiento |
| setSeed(1234L) | Permite obtener resultados reproducibles |
| setMaxIter(100) | Define el número máximo de iteraciones de entrenamiento |

El parámetro `setMaxIter(100)` indica que el algoritmo puede realizar hasta 100 iteraciones para ajustar los pesos internos de la red neuronal.

## Entrenamiento del modelo

El entrenamiento se realiza con la siguiente instrucción:

```scala
val model = trainer.fit(training)
```

En esta etapa, el modelo analiza los datos de entrenamiento y aprende patrones a partir de las características y etiquetas reales.

Durante el entrenamiento, la red neuronal ajusta sus pesos internos para reducir el error entre la predicción realizada y la clase real del registro.

## Predicción sobre los datos de prueba

Después del entrenamiento, el modelo se utiliza para realizar predicciones sobre el conjunto de prueba:

```scala
val result = model.transform(test)
```

El resultado contiene las columnas originales del dataset y además columnas generadas por el modelo, como:

| Columna | Descripción |
|---|---|
| label | Clase real del registro |
| features | Características del registro |
| prediction | Clase predicha por el modelo |
| probability | Probabilidad asociada a cada clase |
| rawPrediction | Valores internos antes de calcular la predicción final |

Posteriormente se seleccionan las columnas necesarias para evaluar el modelo:

```scala
val predictionAndLabels = result.select("prediction", "label")
```

Estas columnas permiten comparar la predicción realizada por el modelo contra la clase real.

## Evaluación del modelo

Para evaluar el modelo se utiliza la clase:

```scala
MulticlassClassificationEvaluator
```

La métrica seleccionada fue:

```scala
accuracy
```

La configuración del evaluador fue la siguiente:

```scala
val evaluator = new MulticlassClassificationEvaluator()
  .setMetricName("accuracy")
```

La exactitud o `accuracy` mide el porcentaje de predicciones correctas realizadas por el modelo.

Su fórmula general es:

```text
Accuracy = Predicciones correctas / Total de predicciones
```

Mientras más cercano sea el valor a 1, mejor será el desempeño del modelo. Un valor de 1 representa un 100% de predicciones correctas.

## Resultado obtenido

Al ejecutar el código, se obtuvo un resultado similar al siguiente:

```text
Test set accuracy = 0.9019607843137255
```

Este resultado significa que el modelo obtuvo una exactitud aproximada del 90%.

En otras palabras, el modelo clasificó correctamente cerca del 90% de los registros del conjunto de prueba.

## Interpretación del resultado

El resultado obtenido puede considerarse satisfactorio, ya que una exactitud cercana al 90% indica que el modelo logró aprender correctamente varios patrones del dataset.

Sin embargo, también significa que existe un porcentaje de registros que no fueron clasificados correctamente. Esto puede deberse a diferentes factores, como:

- La cantidad de datos disponibles.
- La forma en que se dividieron los datos de entrenamiento y prueba.
- La arquitectura de la red neuronal.
- El número de iteraciones configurado.
- La complejidad del problema de clasificación.

En esta práctica se utilizó una red neuronal relativamente pequeña, con dos capas ocultas de 5 y 4 neuronas. A pesar de esto, el modelo logró obtener un desempeño aceptable.

## Funcionamiento general del algoritmo

El funcionamiento del Multilayer Perceptron Classifier se puede resumir en las siguientes etapas:

1. El modelo recibe las características de entrada.
2. Las características pasan por la capa de entrada.
3. La información se procesa en las capas ocultas.
4. Cada neurona realiza cálculos utilizando pesos internos.
5. El resultado pasa hacia la siguiente capa.
6. La capa de salida genera una predicción.
7. La predicción se compara con la clase real.
8. El modelo ajusta sus pesos internos para reducir el error.
9. El proceso se repite durante varias iteraciones.
10. Finalmente, el modelo entrenado puede clasificar nuevos datos.

Este proceso permite que la red neuronal aprenda patrones a partir de los datos y pueda realizar predicciones sobre registros que no fueron utilizados durante el entrenamiento.

## Observaciones de la práctica

Durante la práctica se observó que Apache Spark facilita el entrenamiento de modelos de Machine Learning mediante la librería `spark.ml`.

También se observó que el formato LIBSVM es leído directamente por Spark, lo cual permite trabajar con datasets estructurados para clasificación.

La arquitectura de la red neuronal es un punto importante, ya que debe coincidir con las características del dataset y con el número de clases que se desean predecir.

En este caso, la red neuronal se configuró con 4 neuronas de entrada y 3 neuronas de salida. Esto coincide con el dataset utilizado, ya que contiene 4 características y 3 clases posibles.

El uso de semillas en `randomSplit` y en el clasificador permite que los resultados sean reproducibles.

También se identificó que el valor de `accuracy` permite evaluar de manera sencilla el rendimiento del modelo, ya que indica directamente qué proporción de registros fueron clasificados correctamente.

## Posibles errores encontrados

Uno de los errores más comunes puede ser que Spark no encuentre el archivo del dataset.

Esto puede ocurrir si la ruta configurada no existe:

```text
C:/Spark/data/mllib/sample_multiclass_classification_data.txt
```

En ese caso, se debe verificar que el archivo se encuentre realmente en esa ubicación.

También se puede utilizar una ruta relativa si se está trabajando desde la carpeta principal de Spark:

```scala
.load("data/mllib/sample_multiclass_classification_data.txt")
```

Otro posible error puede estar relacionado con la ejecución del código fuera de `spark-shell`. Este código está preparado para ejecutarse en un entorno donde ya exista una sesión de Spark llamada `spark`.

Por esa razón, se recomienda ejecutar la práctica desde:

```bash
spark-shell
```

o mediante un archivo Scala cargado con:

```bash
spark-shell -i MultilayerPerceptronExample.scala
```

## Conclusión

Se logró ejecutar correctamente el ejemplo de `MultilayerPerceptronClassifier` en Apache Spark usando Scala.

La práctica permitió comprender el proceso completo de un modelo de Machine Learning en Spark: carga de datos, división del dataset, configuración del algoritmo, entrenamiento del modelo, generación de predicciones y evaluación del resultado.

El algoritmo utilizado corresponde a una red neuronal multicapa, capaz de resolver problemas de clasificación multiclase.

El resultado obtenido fue una exactitud aproximada del 90%, lo que indica que el modelo logró clasificar correctamente la mayoría de los registros del conjunto de prueba.

Esta práctica demuestra que Spark MLlib es una herramienta útil para implementar algoritmos de Machine Learning de forma distribuida, estructurada y relativamente sencilla.