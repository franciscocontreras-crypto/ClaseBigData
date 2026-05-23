# Práctica - Multilayer Perceptron Classifier con Iris

## Objetivo

Cargar y preparar el dataset `Iris.csv` utilizando Spark y aplicar el algoritmo de Machine Learning `MultilayerPerceptronClassifier` de Spark MLlib para clasificar especies de flores Iris.

---

# 1. Carga del archivo Iris.csv

Se cargó el archivo `Iris.csv` en un DataFrame de Spark. Fue necesario especificar manualmente el separador.

## Código utilizado

```scala
val iris = spark.read
  .option("header","true")
  .option("inferSchema","true")
  .option("sep","\t")
  .csv("Iris.csv")
```

## Validación de carga

```scala
iris.show(5, false)
```

## Resultado

```text
+------------+-----------+------------+-----------+-------+
|sepal_length|sepal_width|petal_length|petal_width|species|
+------------+-----------+------------+-----------+-------+
|5.1         |3.5        |1.4         |0.2        |setosa |
|4.9         |3.0        |1.4         |0.2        |setosa |
|4.7         |3.2        |1.3         |0.2        |setosa |
|4.6         |3.1        |1.5         |0.2        |setosa |
|5.0         |3.6        |1.4         |0.2        |setosa |
+------------+-----------+------------+-----------+-------+
```

---

# 2. Nombres de las columnas

Para obtener los nombres de las columnas del DataFrame se utilizó:

```scala
iris.columns
```

## Resultado

```scala
Array(
  sepal_length,
  sepal_width,
  petal_length,
  petal_width,
  species
)
```

## Observaciones

Las primeras cuatro columnas corresponden a características numéricas de la flor Iris:

- Largo del sépalo
- Ancho del sépalo
- Largo del pétalo
- Ancho del pétalo

La columna `species` representa la especie de la flor y será utilizada como variable objetivo para clasificación.

---

# 3. Esquema del DataFrame

Para conocer la estructura del DataFrame y el tipo de dato de cada columna, se utilizó:

```scala
iris.printSchema()
```

## Resultado

```text
root
 |-- sepal_length: double (nullable = true)
 |-- sepal_width: double (nullable = true)
 |-- petal_length: double (nullable = true)
 |-- petal_width: double (nullable = true)
 |-- species: string (nullable = true)
```

## Observaciones

El esquema muestra que las variables de medición de la flor son numéricas, mientras que `species` es una variable categórica de texto.

---

# 4. Visualización de registros

Para visualizar los primeros registros del DataFrame se utilizó:

```scala
iris.show(5,false)
```

## Resultado

```text
+------------+-----------+------------+-----------+-------+
|sepal_length|sepal_width|petal_length|petal_width|species|
+------------+-----------+------------+-----------+-------+
|5.1         |3.5        |1.4         |0.2        |setosa |
|4.9         |3.0        |1.4         |0.2        |setosa |
|4.7         |3.2        |1.3         |0.2        |setosa |
|4.6         |3.1        |1.5         |0.2        |setosa |
|5.0         |3.6        |1.4         |0.2        |setosa |
+------------+-----------+------------+-----------+-------+
```

Esto permitió verificar visualmente la correcta carga y estructura de los datos.

---

# 5. Estadísticas descriptivas

Para obtener un resumen estadístico de los datos se utilizó:

```scala
iris.describe().show()
```

## Resultado

```text
+-------+------------------+-------------------+------------------+------------------+---------+
|summary|      sepal_length|        sepal_width|      petal_length|       petal_width|  species|
+-------+------------------+-------------------+------------------+------------------+---------+
|  count|               150|                150|               150|               150|      150|
|   mean| 5.843333333333335| 3.0540000000000007|3.7586666666666693|1.1986666666666672|     NULL|
| stddev|0.8280661279778637|0.43359431136217375| 1.764420419952262|0.7631607417008414|     NULL|
|    min|               4.3|                2.0|               1.0|               0.1|   setosa|
|    max|               7.9|                4.4|               6.9|               2.5|virginica|
+-------+------------------+-------------------+------------------+------------------+---------+
```

## Observaciones

El DataFrame contiene 150 registros.

Las columnas numéricas muestran:
- promedio
- desviación estándar
- valores mínimos
- valores máximos

En la columna `species`, los valores `mean` y `stddev` aparecen como `NULL` debido a que se trata de una variable categórica.

---

# 6. Transformación de etiquetas categóricas

Para utilizar la columna `species` dentro del modelo de Machine Learning, fue necesario convertirla de texto a valor numérico.

## Código utilizado

```scala
import org.apache.spark.ml.feature.StringIndexer

val indexer = new StringIndexer()
  .setInputCol("species")
  .setOutputCol("label")

val indexed = indexer.fit(iris).transform(iris)
```

## Validación

```scala
indexed.select("species","label").show(10,false)
```

## Resultado

```text
+-------+-----+
|species|label|
+-------+-----+
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
|setosa |0.0  |
+-------+-----+
```

La columna `label` representa la clase numérica correspondiente a cada especie.

---

# 7. Construcción de la columna features

Las variables numéricas fueron agrupadas en una sola columna llamada `features`.

## Código utilizado

```scala
import org.apache.spark.ml.feature.VectorAssembler

val assembler = new VectorAssembler()
  .setInputCols(Array(
    "sepal_length",
    "sepal_width",
    "petal_length",
    "petal_width"
  ))
  .setOutputCol("features")

val assembled = assembler.transform(indexed)
```

## Validación

```scala
assembled.select("features","label").show(5,false)
```

## Resultado

```text
+-----------------+-----+
|features         |label|
+-----------------+-----+
|[5.1,3.5,1.4,0.2]|0.0  |
|[4.9,3.0,1.4,0.2]|0.0  |
|[4.7,3.2,1.3,0.2]|0.0  |
|[4.6,3.1,1.5,0.2]|0.0  |
|[5.0,3.6,1.4,0.2]|0.0  |
+-----------------+-----+
```

La columna `features` contiene las cuatro mediciones de cada flor en formato vector.

---

# 8. División de datos

Se creó un DataFrame final llamado `dataML`, conservando únicamente las columnas necesarias para Machine Learning.

## Código utilizado

```scala
val dataML = assembled.select("features","label")

val Array(training, test) =
  dataML.randomSplit(Array(0.7, 0.3), seed = 12345)
```

## Validación

```scala
training.count()
test.count()
```

## Resultado

```text
training: 116 registros
test: 34 registros
```

La división permite entrenar el modelo y posteriormente evaluar su desempeño con datos no utilizados durante el entrenamiento.

---

# 9. Construcción del modelo neuronal

Se utilizó el algoritmo `MultilayerPerceptronClassifier` de Spark MLlib.

## Arquitectura de la red

```scala
val layers = Array[Int](4, 5, 4, 3)
```

Arquitectura utilizada:

```text
4 → 5 → 4 → 3
```

### Interpretación

- `4`: variables de entrada
- `5`: primera capa oculta
- `4`: segunda capa oculta
- `3`: clases de salida

---

## Configuración del modelo

```scala
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier

val trainer = new MultilayerPerceptronClassifier()
  .setLayers(layers)
  .setLabelCol("label")
  .setFeaturesCol("features")
  .setMaxIter(100)
```

## Entrenamiento

```scala
val model = trainer.fit(training)
```

Spark confirmó:

```text
numLayers=4
numClasses=3
numFeatures=4
```

---

# 10. Resultados del modelo

El modelo entrenado fue aplicado sobre el conjunto de prueba.

## Código utilizado

```scala
val predictions = model.transform(test)
```

## Validación

```scala
predictions.select(
  "features",
  "label",
  "prediction"
).show(10,false)
```

## Resultado

```text
+-----------------+-----+----------+
|features         |label|prediction|
+-----------------+-----+----------+
|[4.6,3.2,1.4,0.2]|0.0  |0.0       |
|[4.8,3.1,1.6,0.2]|0.0  |0.0       |
|[4.9,2.5,4.5,1.7]|2.0  |2.0       |
|[5.0,3.0,1.6,0.2]|0.0  |0.0       |
|[5.0,3.2,1.2,0.2]|0.0  |0.0       |
|[5.0,3.5,1.3,0.3]|0.0  |0.0       |
|[5.1,3.5,1.4,0.3]|0.0  |0.0       |
|[5.4,3.4,1.5,0.4]|0.0  |0.0       |
|[5.4,3.9,1.3,0.4]|0.0  |0.0       |
|[5.7,2.8,4.1,1.3]|1.0  |1.0       |
+-----------------+-----+----------+
```

Las predicciones coinciden en la mayoría de los casos con la etiqueta real.

---

# 11. Evaluación del modelo

Para evaluar el desempeño del modelo se utilizó `MulticlassClassificationEvaluator`.

## Código utilizado

```scala
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

val evaluator = new MulticlassClassificationEvaluator()
  .setLabelCol("label")
  .setPredictionCol("prediction")
  .setMetricName("accuracy")

val accuracy = evaluator.evaluate(predictions)

println(s"Accuracy = $accuracy")
```

## Resultado

```text
Accuracy = 0.9705882352941176
```

## Interpretación

La precisión obtenida fue aproximadamente:

```text
97.06%
```

Esto indica que el modelo clasificó correctamente la gran mayoría de las flores del conjunto de prueba.

---

# 12. Observaciones finales

Durante la práctica se realizó el proceso completo de preparación y clasificación de datos utilizando Spark MLlib y el algoritmo `MultilayerPerceptronClassifier`.

## Principales observaciones

- El archivo `Iris.csv` utilizaba tabulaciones (`\t`) como separador y no comas.
- La columna categórica `species` fue transformada a etiquetas numéricas mediante `StringIndexer`.
- Las variables numéricas fueron agrupadas en una columna vectorial llamada `features`.
- Se construyó una red neuronal multicapa con arquitectura:

```text
4 → 5 → 4 → 3
```

- El modelo obtuvo una precisión cercana al 97%, mostrando un desempeño muy alto para clasificar las especies del dataset Iris.