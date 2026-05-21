# Práctica: Multilayer Perceptron Classifier con Apache Spark

## Descripción

En esta práctica se ejecutó un ejemplo de la documentación oficial de Apache Spark correspondiente al algoritmo **Multilayer Perceptron Classifier** usando Scala.

El objetivo fue cargar un conjunto de datos en formato LIBSVM, dividirlo en datos de entrenamiento y prueba, entrenar una red neuronal multicapa y evaluar su desempeño mediante la métrica de exactitud o `accuracy`.

## Algoritmo utilizado

El algoritmo utilizado fue `MultilayerPerceptronClassifier`.

Este algoritmo pertenece a la librería `spark.ml.classification` y permite crear una red neuronal multicapa para resolver problemas de clasificación.

## Dataset utilizado

Se utilizó el archivo `sample_multiclass_classification_data.txt`.

Ruta utilizada:

`C:/Spark/data/mllib/sample_multiclass_classification_data.txt`

El archivo se encuentra en formato **LIBSVM**, el cual es un formato común para representar datasets utilizados en machine learning.

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