# Práctica de Regresión Logística con Spark MLlib

## 1. Carga del archivo Advertising

Se cargó el archivo `advertising.csv` utilizando Spark, con encabezados e inferencia automática de tipos de datos.

```scala
val data = spark.read
  .option("header","true")
  .option("inferSchema", "true")
  .format("csv")
  .load("advertising.csv")
```

---

## 2. Esquema del DataFrame

Para revisar la estructura del DataFrame, se utilizó:

```scala
data.printSchema()
```

Resultado:

```text
root
 |-- Daily Time Spent on Site: double (nullable = true)
 |-- Age: integer (nullable = true)
 |-- Area Income: double (nullable = true)
 |-- Daily Internet Usage: double (nullable = true)
 |-- Ad Topic Line: string (nullable = true)
 |-- City: string (nullable = true)
 |-- Male: integer (nullable = true)
 |-- Country: string (nullable = true)
 |-- Timestamp: timestamp (nullable = true)
 |-- Clicked on Ad: integer (nullable = true)
```

El esquema muestra variables numéricas, categóricas y la variable objetivo `Clicked on Ad`.

---

## 3. Visualización de un registro de ejemplo

Para revisar un primer registro del DataFrame, se utilizó:

```scala
data.head(1)
```

Resultado:

```text
Array([68.95,35,61833.9,256.09,Cloned 5thgeneration orchestration,Wrightburgh,0,Tunisia,2016-03-27 00:53:11.0,0])
```

Esto permitió verificar la correcta carga de los datos.

---

## 4. Despliegue detallado de un registro

Se imprimió un registro de ejemplo junto con el nombre de cada columna utilizando el siguiente bloque:

```scala
val colnames = data.columns
val firstrow = data.head(1)(0)

println("\n")
println("Example data row")

for(ind <- Range(1, colnames.length)){
    println(colnames(ind))
    println(firstrow(ind))
    println("\n")
}
```

Resultado:

```text
Age
35

Area Income
61833.9

Daily Internet Usage
256.09

Ad Topic Line
Cloned 5thgeneration orchestration

City
Wrightburgh

Male
0

Country
Tunisia

Timestamp
2016-03-27 00:53:11.0

Clicked on Ad
0
```

Este despliegue facilita la interpretación individual de cada variable del dataset.

---

## 5. Creación de la columna Hour

Se creó una nueva columna llamada `Hour`, tomando la hora desde la columna `Timestamp`.

```scala
val timedata = data.withColumn("Hour", hour(data("Timestamp")))
```

Esta transformación permite utilizar la hora del evento como una variable predictora adicional.

---

## 6. Preparación del DataFrame para regresión logística

Se creó un nuevo DataFrame llamado `logregdata`, renombrando la columna `Clicked on Ad` como `label`.

Además, se seleccionaron únicamente las variables utilizadas por el modelo:

```scala
val logregdata = timedata.select(
  data("Clicked on Ad").as("label"),
  $"Daily Time Spent on Site",
  $"Age",
  $"Area Income",
  $"Daily Internet Usage",
  $"Hour",
  $"Male"
)
```

El DataFrame quedó compuesto por la variable objetivo y las variables predictoras numéricas.

---

## 7. Verificación del DataFrame preparado

Para verificar la estructura final del DataFrame:

```scala
logregdata.show(5, false)
```

Resultado:

```text
+-----+------------------------+---+-----------+--------------------+----+----+
|label|Daily Time Spent on Site|Age|Area Income|Daily Internet Usage|Hour|Male|
+-----+------------------------+---+-----------+--------------------+----+----+
|0    |68.95                   |35 |61833.9    |256.09              |0   |0   |
|0    |80.23                   |31 |68441.85   |193.77              |1   |1   |
|0    |69.47                   |26 |59785.94   |236.5               |20  |0   |
|0    |74.15                   |29 |54806.18   |245.89              |2   |1   |
|0    |68.37                   |35 |73889.99   |225.58              |3   |0   |
+-----+------------------------+---+-----------+--------------------+----+----+
```

---

## 8. Construcción del vector de características

Se creó un objeto `VectorAssembler` para unir las variables predictoras en una sola columna llamada `features`.

```scala
val assembler = (new VectorAssembler()
  .setInputCols(Array(
    "Daily Time Spent on Site",
    "Age",
    "Area Income",
    "Daily Internet Usage",
    "Hour",
    "Male"
  ))
  .setOutputCol("features"))
```

Este paso es necesario porque Spark MLlib trabaja con vectores de características.

---

## 9. División de datos en entrenamiento y prueba

Se utilizó `randomSplit` para dividir los datos:

- 70% entrenamiento
- 30% prueba

```scala
val Array(training, test) = logregdata.randomSplit(Array(0.7, 0.3), seed = 12345)
```

Verificación de registros:

```scala
training.count()
test.count()
```

Resultado:

```text
training: 713 registros
test: 287 registros
```

---

## 10. Configuración del Pipeline

Se importó `Pipeline` para organizar el flujo de Machine Learning.

```scala
import org.apache.spark.ml.Pipeline
```

Posteriormente, se creó el modelo de regresión logística:

```scala
val lr = new LogisticRegression()
```

Finalmente, se configuró el Pipeline:

```scala
val pipeline = new Pipeline().setStages(Array(assembler, lr))
```

El Pipeline contiene:
- `assembler`: crea la columna `features`
- `lr`: entrena el modelo de regresión logística

---

## 11. Entrenamiento del modelo

El Pipeline fue ajustado utilizando el conjunto de entrenamiento.

```scala
val model = pipeline.fit(training)
```

El resultado fue un objeto `PipelineModel`.

---

## 12. Generación de predicciones

El modelo entrenado fue aplicado sobre el conjunto de prueba:

```scala
val results = model.transform(test)
```

Posteriormente, se visualizaron las columnas `label`, `prediction` y `probability`.

```scala
results.select("label","prediction","probability").show(5, false)
```

Resultado:

```text
+-----+----------+-----------------------------------------+
|label|prediction|probability                              |
+-----+----------+-----------------------------------------+
|0    |0.0       |[0.9067176877142419,0.09328231228575812] |
|0    |0.0       |[0.9677171079714821,0.03228289202851786] |
|0    |0.0       |[0.9553735383754591,0.04462646162454087] |
|0    |0.0       |[0.9685990523107203,0.0314009476892797]  |
|0    |0.0       |[0.9697613540196448,0.030238645980355194]|
+-----+----------+-----------------------------------------+
```

La columna `probability` representa la probabilidad calculada por el modelo para cada clase.

---

## 13. Evaluación del modelo

Se importó `MulticlassMetrics` para evaluar el desempeño del modelo.

```scala
import org.apache.spark.mllib.evaluation.MulticlassMetrics
```

Se creó un RDD con predicciones y etiquetas reales:

```scala
val predictionAndLabels = results.select($"prediction",$"label").as[(Double, Double)].rdd
```

Posteriormente, se inicializó el objeto de métricas:

```scala
val metrics = new MulticlassMetrics(predictionAndLabels)
```

Se imprimió la matriz de confusión:

```scala
println(metrics.confusionMatrix)
```

Resultado:

```text
136.0  1.0
4.0    146.0
```

También se calculó la exactitud del modelo:

```scala
println(metrics.accuracy)
```

Resultado:

```text
0.9825783972125436
```

El modelo obtuvo una exactitud aproximada de **98.26%**, indicando un buen desempeño para clasificar si un usuario hizo clic o no en un anuncio.
