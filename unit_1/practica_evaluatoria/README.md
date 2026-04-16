# Práctica Evaluatoria - Spark DataFrame

## 1. Inicio de sesión en Spark

Se inició una sesión interactiva de Apache Spark mediante el comando:

```bash
spark-shell

## 2. Carga del archivo CSV en un DataFrame

Se cargó el archivo `Netflix_2011_2016.csv` en un DataFrame llamado `df`, utilizando inferencia automática de tipos de datos.

```scala
val df = spark.read.option("header","true").option("inferSchema","true").csv("C:/Users/Latitude/Documents/proyectos/BigData/Spark_DataFrame/Netflix_2011_2016.csv")


## 3. Nombres de las columnas

Para conocer los nombres de las columnas del DataFrame, se utilizó el siguiente comando:

```scala
df.columns

Como resultado, se identificaron las siguientes columnas:

Date
Open
High
Low
Close
Volume
Adj Close


## 4. Esquema del DataFrame

Para conocer la estructura y tipos de datos del DataFrame, se utilizó el siguiente comando:

```scala
df.printSchema()

root
 |-- Date: date (nullable = true)
 |-- Open: double (nullable = true)
 |-- High: double (nullable = true)
 |-- Low: double (nullable = true)
 |-- Close: double (nullable = true)
 |-- Volume: integer (nullable = true)
 |-- Adj Close: double (nullable = true)


 ## 5. Visualización de los primeros registros

Para visualizar los primeros registros del DataFrame, se utilizó el siguiente comando:

```scala
df.show(5, false)

+----------+----------+------------------+----------+-----------------+---------+------------------+
|Date      |Open      |High              |Low       |Close            |Volume   |Adj Close         |
+----------+----------+------------------+----------+-----------------+---------+------------------+
|2011-10-24|119.100002|120.28000300000001|115.100004|118.839996       |120460200|16.977142         |
|2011-10-25|74.899999 |79.390001         |74.249997 |77.370002        |315541800|11.052857000000001|
|2011-10-26|78.73     |81.420001         |75.399997 |79.400002        |148733900|11.342857         |
|2011-10-27|82.179998 |82.71999699999999 |79.249998 |80.86000200000001|71190000 |11.551428999999999|
|2011-10-28|80.280002 |84.660002         |79.599999 |84.14000300000001|57769600 |12.02             |
+----------+----------+------------------+----------+-----------------+---------+------------------+

## 6. Estadísticas descriptivas del DataFrame

Para obtener un resumen estadístico de las columnas numéricas del DataFrame, se utilizó el siguiente comando:

```scala
df.describe().show()

Se cuenta con un total de 1259 registros en el dataset.
El precio promedio de apertura (Open) es aproximadamente 230.39.
El valor máximo registrado en la columna High es cercano a 716.15.
El volumen máximo de transacciones (Volume) alcanzó más de 315 millones.
Los valores mínimos de precios se encuentran alrededor de los 50 USD, lo que refleja una gran variación en el comportamiento histórico de la acción.


## 7. Creación de nueva columna "HV Ratio"

Se creó un nuevo DataFrame con una columna adicional llamada **HV Ratio**, que representa la relación entre el valor de la columna **High** y el volumen de acciones negociadas (**Volume**).

```scala
val df2 = df.withColumn("HV Ratio", $"High" / $"Volume")

df2.select("Date","High","Volume","HV Ratio").show(5, false)

+----------+------------------+---------+----------------------+
|Date      |High              |Volume   |HV Ratio              |
+----------+------------------+---------+----------------------+
|2011-10-24|120.28000300000001|120460200|9.985040951285156E-7  |
|2011-10-25|79.390001         |315541800|2.5159898989281927E-7 |
|2011-10-26|81.420001         |148733900|5.474206014903126E-7  |
|2011-10-27|82.71999699999999 |71190000 |1.1619609074308188E-6 |
|2011-10-28|84.660002         |57769600 |1.4654766867002715E-6 |
+----------+------------------+---------+----------------------+