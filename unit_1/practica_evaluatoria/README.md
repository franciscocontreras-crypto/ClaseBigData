# Práctica Evaluatoria - Spark DataFrame

## 1. Inicio de sesión en Spark

Se inició una sesión interactiva de Apache Spark mediante el comando:

```bash
spark-shell
```

---

## 2. Carga del archivo CSV en un DataFrame

Se cargó el archivo `Netflix_2011_2016.csv` en un DataFrame llamado `df`, utilizando inferencia automática de tipos de datos.

```scala
val df = spark.read
  .option("header","true")
  .option("inferSchema","true")
  .csv("C:/Users/Latitude/Documents/proyectos/BigData/Spark_DataFrame/Netflix_2011_2016.csv")
```

---

## 3. Nombres de las columnas

Para conocer los nombres de las columnas del DataFrame, se utilizó el siguiente comando:

```scala
df.columns
```

Como resultado, se identificaron las siguientes columnas:

* Date
* Open
* High
* Low
* Close
* Volume
* Adj Close

---

## 4. Esquema del DataFrame

Para conocer la estructura y tipos de datos del DataFrame, se utilizó el siguiente comando:

```scala
df.printSchema()
```

Resultado:

```
root
 |-- Date: date (nullable = true)
 |-- Open: double (nullable = true)
 |-- High: double (nullable = true)
 |-- Low: double (nullable = true)
 |-- Close: double (nullable = true)
 |-- Volume: integer (nullable = true)
 |-- Adj Close: double (nullable = true)
```

---

## 5. Visualización de los primeros registros

Para visualizar los primeros registros del DataFrame:

```scala
df.show(5, false)
```

Resultado:

```
+----------+----------+------------------+----------+-----------------+---------+------------------+
|Date      |Open      |High              |Low       |Close            |Volume   |Adj Close         |
+----------+----------+------------------+----------+-----------------+---------+------------------+
|2011-10-24|119.100002|120.28000300000001|115.100004|118.839996       |120460200|16.977142         |
|2011-10-25|74.899999 |79.390001         |74.249997 |77.370002        |315541800|11.052857000000001|
|2011-10-26|78.73     |81.420001         |75.399997 |79.400002        |148733900|11.342857         |
|2011-10-27|82.179998 |82.71999699999999 |79.249998 |80.86000200000001|71190000 |11.551428999999999|
|2011-10-28|80.280002 |84.660002         |79.599999 |84.14000300000001|57769600 |12.02             |
+----------+----------+------------------+----------+-----------------+---------+------------------+
```

---

## 6. Estadísticas descriptivas del DataFrame

Para obtener un resumen estadístico:

```scala
df.describe().show()
```

Conclusiones:

* Se cuenta con un total de **1259 registros** en el dataset.
* El precio promedio de apertura (**Open**) es aproximadamente **230.39**.
* El valor máximo en la columna **High** es cercano a **716.15**.
* El volumen máximo (**Volume**) supera los **315 millones**.
* Los valores mínimos de precios rondan los **50 USD**, mostrando alta variabilidad.

---

## 7. Creación de nueva columna "HV Ratio"

Se creó una nueva columna que representa la relación entre el valor **High** y el volumen de transacciones.

```scala
val df2 = df.withColumn("HV Ratio", $"High" / $"Volume")

df2.select("Date","High","Volume","HV Ratio").show(5, false)
```

Resultado:

```
+----------+------------------+---------+----------------------+
|Date      |High              |Volume   |HV Ratio              |
+----------+------------------+---------+----------------------+
|2011-10-24|120.28000300000001|120460200|9.985040951285156E-7  |
|2011-10-25|79.390001         |315541800|2.5159898989281927E-7 |
|2011-10-26|81.420001         |148733900|5.474206014903126E-7  |
|2011-10-27|82.71999699999999 |71190000 |1.1619609074308188E-6 |
|2011-10-28|84.660002         |57769600 |1.4654766867002715E-6 |
+----------+------------------+---------+----------------------+
```

---

## 8. Día con el valor más alto en "Open"

```scala
df.orderBy($"Open".desc)
  .select("Date", "Open")
  .show(1, false)
```

Se identificó el día con el valor máximo de apertura en el periodo analizado.

---

## 9. Significado de la columna "Close"

La columna **Close** representa el **precio de cierre de la acción al final del día bursátil**.

Este valor es importante porque:

* Refleja el último precio negociado del día
* Se usa para análisis financiero y tendencias
* Sirve como base para calcular rendimientos

---

## 10. Máximo y mínimo de "Volume"

```scala
df.select(
  max($"Volume").alias("Max_Volume"),
  min($"Volume").alias("Min_Volume")
).show()
```

Se obtuvieron los valores máximo y mínimo del volumen de transacciones.

---

## 11. Análisis con Sintaxis Scala/Spark `$`

### a) Días con "Close" < 600

```scala
df.filter($"Close" < 600).count()
```

---

### b) Porcentaje de días con "High" > 500

```scala
val total = df.count()
val high500 = df.filter($"High" > 500).count()
val porcentaje = (high500.toDouble / total) * 100

porcentaje
```

---

### c) Correlación entre "High" y "Volume"

```scala
df.stat.corr("High", "Volume")
```

---

### d) Máximo "High" por año

```scala
val dfYear = df.withColumn("Year", year($"Date"))

dfYear.groupBy("Year")
  .max("High")
  .orderBy("Year")
  .show()
```

---

### e) Promedio "Close" por mes

```scala
val dfMonth = df.withColumn("Month", month($"Date"))

dfMonth.groupBy("Month")
  .avg("Close")
  .orderBy("Month")
  .show()
```

---

## Conclusión

Este análisis permitió explorar el comportamiento histórico de la acción de Netflix mediante el uso de Apache Spark y DataFrames, destacando la eficiencia en el procesamiento de datos y la facilidad para realizar transformaciones y agregaciones sobre grandes volúmenes de información.
