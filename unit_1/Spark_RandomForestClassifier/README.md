# Práctica 5 - Random Forest Classifier

## 1. Objetivo

Ejecutar el ejemplo de Random Forest Classifier de la documentación de Spark y documentar las observaciones principales del proceso.

---

## 2. Librerías utilizadas

Se importaron las clases necesarias para trabajar con Random Forest en Spark MLlib:

```scala
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
```

---

## 3. Carga de datos

Se utilizó el archivo de ejemplo `sample_libsvm_data.txt`, ubicado en la instalación local de Spark.

```scala
val data = MLUtils.loadLibSVMFile(sc, "c:/spark/data/mllib/sample_libsvm_data.txt")
```

Este archivo se encuentra en formato `LIBSVM`, el cual permite representar datos etiquetados con sus respectivas características.

---

## 4. División de datos

Los datos fueron divididos en dos conjuntos:

- 70% para entrenamiento
- 30% para prueba

```scala
val splits = data.randomSplit(Array(0.7, 0.3))
val (trainingData, testData) = (splits(0), splits(1))
```

---

## 5. Configuración del modelo Random Forest

Se configuró un modelo de clasificación Random Forest con los siguientes parámetros:

```scala
val numClasses = 2
val categoricalFeaturesInfo = Map[Int, Int]()
val numTrees = 3
val featureSubsetStrategy = "auto"
val impurity = "gini"
val maxDepth = 4
val maxBins = 32
```

Observaciones:

- `numClasses = 2` indica que se trata de un problema de clasificación binaria.
- `numTrees = 3` indica que el modelo utiliza tres árboles de decisión.
- `impurity = "gini"` define el criterio utilizado para dividir los nodos.
- `maxDepth = 4` limita la profundidad máxima de los árboles.

---

## 6. Entrenamiento del modelo

El modelo fue entrenado con el conjunto de entrenamiento:

```scala
val model = RandomForest.trainClassifier(
  trainingData,
  numClasses,
  categoricalFeaturesInfo,
  numTrees,
  featureSubsetStrategy,
  impurity,
  maxDepth,
  maxBins
)
```

---

## 7. Evaluación del modelo

Para evaluar el modelo, se generaron predicciones sobre el conjunto de prueba:

```scala
val labelAndPreds = testData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
```

Después se calculó el error de prueba:

```scala
val testErr = labelAndPreds
  .filter(r => r._1 != r._2)
  .count()
  .toDouble / testData.count()
```

Resultado obtenido:

```text
Test Error = 0.0
```

Este resultado indica que, en esta ejecución, el modelo clasificó correctamente todos los registros del conjunto de prueba.

---

## 8. Modelo aprendido

Spark imprimió el modelo aprendido como un conjunto de tres árboles de decisión:

```text
Learned classification forest model:
TreeEnsembleModel classifier with 3 trees
```

Fragmento del modelo:

```text
Tree 0:
  If (feature 497 <= 1.5)
   If (feature 245 <= 16.0)
    If (feature 631 <= 252.5)
     Predict: 1.0
    Else (feature 631 > 252.5)
     If (feature 483 <= 15.5)
      Predict: 1.0
     Else (feature 483 > 15.5)
      Predict: 0.0
   Else (feature 245 > 16.0)
    Predict: 0.0
  Else (feature 497 > 1.5)
   Predict: 0.0

Tree 1:
  If (feature 517 <= 20.5)
   Predict: 0.0
  Else (feature 517 > 20.5)
   Predict: 1.0

Tree 2:
  If (feature 483 <= 15.5)
   If (feature 433 <= 52.5)
    If (feature 547 <= 8.5)
     Predict: 0.0
    Else (feature 547 > 8.5)
     Predict: 1.0
   Else (feature 433 > 52.5)
    Predict: 1.0
  Else (feature 483 > 15.5)
   Predict: 0.0
```

---

## 9. Observaciones finales

Durante la primera ejecución, Spark no encontró el archivo `sample_libsvm_data.txt` usando la ruta relativa:

```scala
data/mllib/sample_libsvm_data.txt
```

Por ello, se utilizó la ruta absoluta correspondiente a la instalación local de Spark:

```scala
c:/spark/data/mllib/sample_libsvm_data.txt
```

También se comentaron las líneas de guardado y carga del modelo debido a un problema de configuración en Windows relacionado con `HADOOP_HOME` y `winutils.exe`.

Finalmente, la ejecución principal del modelo fue exitosa, obteniendo un `Test Error = 0.0`.