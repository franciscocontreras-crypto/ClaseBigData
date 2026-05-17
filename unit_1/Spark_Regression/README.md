# Práctica de Regresión Lineal con Spark MLlib

## 1. Carga inicial del archivo Clean-Ecommerce

Se cargó el archivo `Clean-Ecommerce.csv` en un DataFrame llamado `data`, utilizando inferencia automática de tipos de datos.

```scala
val data = spark.read
  .option("header","true")
  .option("inferSchema","true")
  .csv("Clean-Ecommerce.csv")
```

---

## 2. Esquema del DataFrame

Para conocer la estructura del DataFrame, se utilizó:

```scala
data.printSchema()
```

El esquema obtenido fue:

```text
root
 |-- Email: string (nullable = true)
 |-- Avatar: string (nullable = true)
 |-- Avg Session Length: double (nullable = true)
 |-- Time on App: double (nullable = true)
 |-- Time on Website: double (nullable = true)
 |-- Length of Membership: double (nullable = true)
 |-- Yearly Amount Spent: double (nullable = true)
```

Spark identificó correctamente columnas de texto y variables numéricas de tipo `double`.

---

## 3. Visualización de un registro de ejemplo

Para revisar un registro del DataFrame, se utilizó el siguiente comando:

```scala
data.show(1, false)
```

Resultado:

```text
+-------------------------+------+------------------+-----------------+-----------------+--------------------+-------------------+
|Email                    |Avatar|Avg Session Length|Time on App      |Time on Website  |Length of Membership|Yearly Amount Spent|
+-------------------------+------+------------------+-----------------+-----------------+--------------------+-------------------+
|mstephenson@fernandez.com|Violet|34.49726772511229 |12.65565114916675|39.57766801952616|4.0826206329529615  |587.9510539684005  |
+-------------------------+------+------------------+-----------------+-----------------+--------------------+-------------------+
```

Esto permitió verificar la correcta carga del dataset.

---

## 4. Preparación inicial del DataFrame para Machine Learning

Se importaron las herramientas necesarias para construir vectores de características:

```scala
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg.Vectors
```

Posteriormente, se creó un nuevo DataFrame llamado `df`, donde la columna `Yearly Amount Spent` fue renombrada como `label`.

```scala
val df = data.select(
  data("Yearly Amount Spent").as("label"),
  $"Avg Session Length",
  $"Time on App",
  $"Time on Website",
  $"Length of Membership"
)
```

Este nuevo DataFrame conserva únicamente columnas numéricas necesarias para el entrenamiento del modelo.

---

## 5. Verificación del nuevo DataFrame

Para verificar la estructura del DataFrame preparado para Machine Learning, se utilizó:

```scala
df.show(5, false)
```

Resultado:

```text
+------------------+------------------+------------------+------------------+--------------------+
|label             |Avg Session Length|Time on App       |Time on Website   |Length of Membership|
+------------------+------------------+------------------+------------------+--------------------+
|587.9510539684005 |34.49726772511229 |12.65565114916675 |39.57766801952616 |4.0826206329529615  |
|392.2049334443264 |31.92627202636016 |11.109460728682564|37.268958868297744|2.66403418213262    |
|487.54750486747207|33.000914755642675|11.330278057777512|37.110597442120856|4.104543202376424   |
|581.8523440352177 |34.30555662975554 |13.717513665142507|36.72128267790313 |3.120178782748092   |
|599.4060920457634 |33.33067252364639 |12.795188551078114|37.53665330059473 |4.446308318351434   |
+------------------+------------------+------------------+------------------+--------------------+
```

---

## 6. Construcción del vector de características

Se creó un objeto `VectorAssembler` para unir las variables numéricas en una sola columna llamada `features`.

```scala
val assembler = new VectorAssembler()
  .setInputCols(Array(
    "Avg Session Length",
    "Time on App",
    "Time on Website",
    "Length of Membership"
  ))
  .setOutputCol("features")
```

Posteriormente, el DataFrame fue transformado para conservar únicamente las columnas `label` y `features`.

```scala
val output = assembler.transform(df).select("label", "features")
```

Para verificar el resultado:

```scala
output.show(5, false)
```

Resultado:

```text
+------------------+----------------------------------------------------------------------------+
|label             |features                                                                    |
+------------------+----------------------------------------------------------------------------+
|587.9510539684005 |[34.49726772511229,12.65565114916675,39.57766801952616,4.0826206329529615]  |
|392.2049334443264 |[31.92627202636016,11.109460728682564,37.268958868297744,2.66403418213262]  |
|487.54750486747207|[33.000914755642675,11.330278057777512,37.110597442120856,4.104543202376424]|
|581.8523440352177 |[34.30555662975554,13.717513665142507,36.72128267790313,3.120178782748092]  |
|599.4060920457634 |[33.33067252364639,12.795188551078114,37.53665330059473,4.446308318351434]  |
+------------------+----------------------------------------------------------------------------+
```

Con esto, los datos quedaron preparados para entrenar el modelo de regresión lineal.

---

## 7. Creación y entrenamiento del modelo de regresión lineal

Se creó un objeto de regresión lineal utilizando:

```scala
val lr = new LinearRegression()
```

Posteriormente, el modelo fue entrenado utilizando el DataFrame `output`.

```scala
val lrModelo = lr.fit(output)
```

Durante el entrenamiento aparecieron advertencias relacionadas con `regParam` y `JNILAPACK`; sin embargo, el modelo fue entrenado correctamente.

---

## 8. Coeficientes e intercepto del modelo

Para obtener los coeficientes e intercepto de la regresión lineal, se ejecutó:

```scala
println(s"Coefficients: ${lrModelo.coefficients} Intercept: ${lrModelo.intercept}")
```

Resultado:

```text
Coefficients: [25.734271084670716,38.709153810828816,0.43673883558514964,61.57732375487594]
Intercept: -1051.5942552990748
```

Estos valores representan el peso de cada variable predictora dentro del modelo.

---

## 9. Métricas del modelo

Se creó un resumen del modelo entrenado:

```scala
val trainingSummary = lrModelo.summary
```

Posteriormente, se revisaron los residuales:

```scala
trainingSummary.residuals.show(5, false)
```

Resultado:

```text
+------------------+
|residuals         |
+------------------+
|-6.788234090018818|
|11.841128565326073|
|-17.65262700858966|
|11.454889631178617|
|7.7833824373080915|
+------------------+
```

También se imprimieron las métricas principales del modelo:

```scala
println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
println(s"MSE: ${trainingSummary.meanSquaredError}")
println(s"R2: ${trainingSummary.r2}")
```

Resultado:

```text
RMSE: 9.923256785022229
MSE: 98.47102522148971
R2: 0.9843155370226727
```

El valor de `R2` indica que el modelo explica aproximadamente el 98.43% de la variación en la variable objetivo `Yearly Amount Spent`.
