# Práctica: Decision Tree Classifier con Apache Spark

## Descripción

En esta práctica se implementó un ejemplo de clasificación utilizando Apache Spark con Scala. El algoritmo utilizado fue **Decision Tree Classifier**, también conocido como clasificador basado en árbol de decisión.

El objetivo principal de esta práctica fue crear un pequeño conjunto de datos relacionado con la aprobación o rechazo de crédito, transformar las columnas de entrada en un vector de características, entrenar un modelo de árbol de decisión y evaluar su desempeño mediante la métrica de exactitud o `accuracy`.

Este ejemplo permite comprender de manera sencilla cómo Spark ML puede utilizarse para resolver problemas de clasificación supervisada.

## Objetivo de la práctica

El objetivo de la práctica fue construir un modelo de Machine Learning que pudiera predecir una etiqueta o clase de salida con base en tres variables de entrada:

- Si la persona tiene ingresos altos.
- Si la persona tiene deuda.
- Si la persona tiene buen historial crediticio.

La variable de salida, llamada `label`, representa si el crédito fue aprobado o no.

## Algoritmo utilizado

El algoritmo utilizado fue:

```scala
DecisionTreeClassifier
```

Este algoritmo pertenece a la librería:

```scala
org.apache.spark.ml.classification
```

El **Decision Tree Classifier** es un algoritmo de clasificación supervisada que funciona mediante una estructura similar a un árbol. Cada nodo del árbol representa una condición sobre una característica del dataset, y cada rama representa una posible decisión tomada a partir de esa condición.

Al final del árbol se encuentran las hojas, las cuales representan la clase o predicción final.

En este caso, el árbol de decisión se utilizó para predecir si un crédito puede ser aprobado o rechazado con base en características simples del solicitante.

## Código ejecutado

```scala
import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder()
  .appName("DecisionTreeCreditExample")
  .getOrCreate()

val data = Seq(
  (1.0,0.0,1.0,1.0),
  (1.0,0.0,1.0,1.0),
  (1.0,1.0,1.0,1.0),
  (1.0,0.0,0.0,0.0),
  (1.0,1.0,0.0,0.0),
  (0.0,1.0,0.0,0.0),
  (0.0,1.0,0.0,0.0),
  (0.0,0.0,1.0,1.0),
  (0.0,1.0,1.0,0.0),
  (0.0,0.0,0.0,0.0)
).toDF("income_high","has_debt","good_history","label")

data.show()

import org.apache.spark.ml.feature.VectorAssembler

val assembler = new VectorAssembler()
  .setInputCols(Array("income_high","has_debt","good_history"))
  .setOutputCol("features")

val dataset = assembler.transform(data)

dataset.select("features","label").show()

val Array(trainingData, testData) = dataset.randomSplit(Array(0.7, 0.3), seed = 42)

import org.apache.spark.ml.classification.DecisionTreeClassifier

val dt = new DecisionTreeClassifier()
  .setLabelCol("label")
  .setFeaturesCol("features")
  .setMaxDepth(3)

val model = dt.fit(trainingData)

println(model.toDebugString)

val predictions = model.transform(testData)

predictions.select("features","label","prediction","probability").show(false)

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

val evaluator = new MulticlassClassificationEvaluator()
  .setLabelCol("label")
  .setPredictionCol("prediction")
  .setMetricName("accuracy")

val accuracy = evaluator.evaluate(predictions)

println("Accuracy = " + accuracy)
```

## Explicación general del código

El código se divide en varias etapas principales:

1. Creación de la sesión de Spark.
2. Creación manual del conjunto de datos.
3. Transformación de columnas en un vector de características.
4. División del dataset en entrenamiento y prueba.
5. Creación del modelo de árbol de decisión.
6. Entrenamiento del modelo.
7. Visualización de la estructura del árbol.
8. Generación de predicciones.
9. Evaluación del modelo mediante `accuracy`.

Cada una de estas etapas forma parte del flujo típico de trabajo en un proyecto de Machine Learning con Spark ML.

## Creación de la sesión de Spark

La práctica inicia creando una sesión de Spark:

```scala
import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder()
  .appName("DecisionTreeCreditExample")
  .getOrCreate()
```

La clase `SparkSession` es el punto de entrada principal para trabajar con Spark SQL, DataFrames y Spark ML.

El método `.appName("DecisionTreeCreditExample")` asigna un nombre a la aplicación de Spark.

El método `.getOrCreate()` obtiene una sesión existente de Spark o crea una nueva si no existe.

## Creación del dataset

El conjunto de datos se creó manualmente usando una secuencia de valores:

```scala
val data = Seq(
  (1.0,0.0,1.0,1.0),
  (1.0,0.0,1.0,1.0),
  (1.0,1.0,1.0,1.0),
  (1.0,0.0,0.0,0.0),
  (1.0,1.0,0.0,0.0),
  (0.0,1.0,0.0,0.0),
  (0.0,1.0,0.0,0.0),
  (0.0,0.0,1.0,1.0),
  (0.0,1.0,1.0,0.0),
  (0.0,0.0,0.0,0.0)
).toDF("income_high","has_debt","good_history","label")
```

El dataset contiene cuatro columnas:

| Columna | Descripción |
|---|---|
| income_high | Indica si la persona tiene ingresos altos |
| has_debt | Indica si la persona tiene deuda |
| good_history | Indica si la persona tiene buen historial crediticio |
| label | Resultado esperado o clase real |

Los valores utilizados son numéricos:

| Valor | Significado |
|---:|---|
| 1.0 | Sí / Verdadero |
| 0.0 | No / Falso |

En este ejemplo, la columna `label` puede interpretarse de la siguiente manera:

| Label | Interpretación |
|---:|---|
| 1.0 | Crédito aprobado |
| 0.0 | Crédito rechazado |

## Visualización inicial de los datos

Después de crear el DataFrame, se ejecuta:

```scala
data.show()
```

Este comando muestra en consola los datos cargados.

Un resultado esperado sería similar al siguiente:

```text
+-----------+--------+------------+-----+
|income_high|has_debt|good_history|label|
+-----------+--------+------------+-----+
|        1.0|     0.0|         1.0|  1.0|
|        1.0|     0.0|         1.0|  1.0|
|        1.0|     1.0|         1.0|  1.0|
|        1.0|     0.0|         0.0|  0.0|
|        1.0|     1.0|         0.0|  0.0|
|        0.0|     1.0|         0.0|  0.0|
|        0.0|     1.0|         0.0|  0.0|
|        0.0|     0.0|         1.0|  1.0|
|        0.0|     1.0|         1.0|  0.0|
|        0.0|     0.0|         0.0|  0.0|
+-----------+--------+------------+-----+
```

## Transformación de características con VectorAssembler

Spark ML requiere que las variables de entrada estén agrupadas en una sola columna de tipo vector. Para esto se utiliza `VectorAssembler`.

```scala
import org.apache.spark.ml.feature.VectorAssembler

val assembler = new VectorAssembler()
  .setInputCols(Array("income_high","has_debt","good_history"))
  .setOutputCol("features")
```

El `VectorAssembler` toma las columnas:

```scala
income_high, has_debt, good_history
```

y las combina en una nueva columna llamada:

```scala
features
```

Después se transforma el dataset:

```scala
val dataset = assembler.transform(data)
```

Finalmente, se muestran las columnas `features` y `label`:

```scala
dataset.select("features","label").show()
```

El resultado esperado sería similar al siguiente:

```text
+-------------+-----+
|     features|label|
+-------------+-----+
|[1.0,0.0,1.0]|  1.0|
|[1.0,0.0,1.0]|  1.0|
|[1.0,1.0,1.0]|  1.0|
|[1.0,0.0,0.0]|  0.0|
|[1.0,1.0,0.0]|  0.0|
|[0.0,1.0,0.0]|  0.0|
|[0.0,1.0,0.0]|  0.0|
|[0.0,0.0,1.0]|  1.0|
|[0.0,1.0,1.0]|  0.0|
|    (3,[],[])|  0.0|
+-------------+-----+
```

En algunos casos, Spark puede mostrar el vector `[0.0,0.0,0.0]` como `(3,[],[])`. Esto significa que es un vector disperso de tamaño 3 donde todos los valores son cero.

## División del dataset

El dataset se divide en datos de entrenamiento y datos de prueba:

```scala
val Array(trainingData, testData) = dataset.randomSplit(Array(0.7, 0.3), seed = 42)
```

Esto significa que:

| Conjunto | Porcentaje | Uso |
|---|---:|---|
| trainingData | 70% | Entrenar el modelo |
| testData | 30% | Evaluar el modelo |

El parámetro `seed = 42` permite que la división de los datos sea reproducible.

Es decir, si se ejecuta nuevamente el código con la misma semilla, Spark realizará la misma separación de datos.

## Creación del modelo Decision Tree

El clasificador se crea con el siguiente código:

```scala
import org.apache.spark.ml.classification.DecisionTreeClassifier

val dt = new DecisionTreeClassifier()
  .setLabelCol("label")
  .setFeaturesCol("features")
  .setMaxDepth(3)
```

Los parámetros configurados son:

| Parámetro | Descripción |
|---|---|
| setLabelCol("label") | Indica cuál columna contiene la clase real |
| setFeaturesCol("features") | Indica cuál columna contiene las características de entrada |
| setMaxDepth(3) | Define la profundidad máxima del árbol |

La profundidad máxima del árbol indica cuántos niveles de decisiones puede tener el modelo.

En este caso se configuró una profundidad máxima de 3, lo cual limita la complejidad del árbol y ayuda a evitar que el modelo se ajuste demasiado a los datos de entrenamiento.

## Entrenamiento del modelo

El entrenamiento se realiza con la siguiente instrucción:

```scala
val model = dt.fit(trainingData)
```

En esta etapa, el algoritmo analiza los datos de entrenamiento y construye un árbol de decisión.

El modelo aprende reglas basadas en las variables de entrada, por ejemplo:

- Si tiene buen historial crediticio.
- Si tiene ingresos altos.
- Si tiene deuda.

Con base en esas condiciones, el árbol aprende a clasificar si el crédito debe aprobarse o rechazarse.

## Funcionamiento del algoritmo Decision Tree

El algoritmo **Decision Tree** funciona dividiendo los datos en grupos más pequeños de acuerdo con condiciones sobre las características.

Por ejemplo, el modelo puede aprender reglas similares a:

```text
Si good_history = 1.0, entonces revisar si has_debt = 0.0.
Si good_history = 0.0, entonces probablemente rechazar el crédito.
Si income_high = 1.0 y good_history = 1.0, entonces aprobar el crédito.
```

Estas reglas no se escriben manualmente. El modelo las aprende automáticamente a partir de los datos de entrenamiento.

El proceso general del algoritmo es:

1. Analiza todas las características disponibles.
2. Selecciona la característica que mejor separa las clases.
3. Crea una condición o división.
4. Divide los datos en ramas.
5. Repite el proceso en cada rama.
6. Detiene el crecimiento del árbol cuando alcanza la profundidad máxima o ya no puede mejorar la clasificación.
7. Genera una predicción final en las hojas del árbol.

Una ventaja de los árboles de decisión es que son fáciles de interpretar, ya que se pueden visualizar como una serie de reglas lógicas.

## Visualización del árbol generado

Después de entrenar el modelo, se imprime la estructura del árbol con:

```scala
println(model.toDebugString)
```

Este comando muestra las reglas aprendidas por el árbol de decisión.

Un resultado esperado puede ser similar a:

```text
DecisionTreeClassificationModel: uid=dtc_xxxxx, depth=2, numNodes=5, numClasses=2, numFeatures=3
  If (feature 2 <= 0.5)
   Predict: 0.0
  Else (feature 2 > 0.5)
   If (feature 1 <= 0.5)
    Predict: 1.0
   Else (feature 1 > 0.5)
    Predict: 0.0
```

La interpretación de este árbol sería:

| Feature | Columna original |
|---|---|
| feature 0 | income_high |
| feature 1 | has_debt |
| feature 2 | good_history |

Por ejemplo:

```text
If (feature 2 <= 0.5)
```

significa:

```text
Si good_history es 0.0
```

Entonces el modelo puede predecir rechazo del crédito.

```text
Else (feature 2 > 0.5)
```

significa:

```text
Si good_history es 1.0
```

Entonces el modelo continúa evaluando otra característica.

Es importante mencionar que el árbol exacto puede variar dependiendo de la división de entrenamiento y prueba realizada por Spark.

## Generación de predicciones

Después del entrenamiento, se generan predicciones sobre los datos de prueba:

```scala
val predictions = model.transform(testData)
```

El modelo toma los registros del conjunto de prueba y genera nuevas columnas con información de predicción.

Posteriormente se muestran las columnas principales:

```scala
predictions.select("features","label","prediction","probability").show(false)
```

Las columnas mostradas son:

| Columna | Descripción |
|---|---|
| features | Características de entrada |
| label | Clase real |
| prediction | Clase predicha por el modelo |
| probability | Probabilidad estimada para cada clase |

Un resultado esperado puede verse similar a:

```text
+-------------+-----+----------+-----------+
|features     |label|prediction|probability|
+-------------+-----+----------+-----------+
|[0.0,0.0,1.0]|1.0  |1.0       |[0.0,1.0]  |
|[1.0,0.0,0.0]|0.0  |0.0       |[1.0,0.0]  |
|[1.0,1.0,1.0]|1.0  |0.0       |[1.0,0.0]  |
+-------------+-----+----------+-----------+
```

La columna `probability` muestra la probabilidad asignada a cada clase. Por ejemplo:

```text
[0.0,1.0]
```

puede interpretarse como:

| Clase | Probabilidad |
|---|---:|
| 0.0 | 0% |
| 1.0 | 100% |

Esto significa que el modelo está prediciendo la clase `1.0`.

## Evaluación del modelo

Para evaluar el modelo se utiliza:

```scala
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

val evaluator = new MulticlassClassificationEvaluator()
  .setLabelCol("label")
  .setPredictionCol("prediction")
  .setMetricName("accuracy")
```

La métrica seleccionada fue:

```scala
accuracy
```

La exactitud o `accuracy` mide la proporción de predicciones correctas realizadas por el modelo.

Su fórmula general es:

```text
Accuracy = Predicciones correctas / Total de predicciones
```

Por ejemplo, si el modelo evaluó 3 registros y clasificó correctamente 2, la exactitud sería:

```text
Accuracy = 2 / 3 = 0.6667
```

Esto equivale aproximadamente a un 66.67%.

## Resultado obtenido

El resultado se calcula con:

```scala
val accuracy = evaluator.evaluate(predictions)
```

Y se imprime con:

```scala
println("Accuracy = " + accuracy)
```

El resultado puede variar dependiendo de cómo Spark divida los datos de entrenamiento y prueba, aunque se haya utilizado una semilla.

Un posible resultado sería:

```text
Accuracy = 0.6666666666666666
```

Esto significa que el modelo clasificó correctamente aproximadamente el 66.67% de los registros del conjunto de prueba.

Debido a que el dataset es muy pequeño, el resultado puede variar bastante. Con pocos datos, uno o dos errores de predicción pueden afectar mucho el valor final de la exactitud.

## Interpretación del resultado

El resultado obtenido indica qué tan bien funcionó el modelo sobre los datos de prueba.

Si el valor de `accuracy` es cercano a 1.0, significa que el modelo clasificó correctamente la mayoría de los registros.

Si el valor es bajo, significa que el modelo tuvo dificultades para predecir correctamente las clases.

En esta práctica, el dataset contiene únicamente 10 registros, por lo que el resultado debe interpretarse con cuidado. Al ser un conjunto de datos pequeño, el modelo no cuenta con suficiente información para aprender patrones más sólidos.

Aun así, la práctica cumple su objetivo, ya que permite observar el flujo completo de entrenamiento y evaluación de un modelo de clasificación en Spark.

## Observaciones de la práctica

Durante la práctica se observó que Spark permite crear un dataset manualmente y convertirlo en un DataFrame usando `.toDF()`.

También se observó que los algoritmos de Spark ML requieren que las variables de entrada estén agrupadas en una columna llamada comúnmente `features`. Para lograr esto se utilizó `VectorAssembler`.

El modelo de árbol de decisión resultó fácil de interpretar, especialmente gracias al método:

```scala
model.toDebugString
```

Este método permite visualizar las reglas aprendidas por el modelo.

Además, se observó que la variable `good_history` puede tener una influencia importante en la predicción, ya que el historial crediticio es una característica relevante para decidir si se aprueba o rechaza un crédito.

También se observó que el valor de `accuracy` depende directamente de los datos de prueba. Como el dataset es pequeño, el resultado puede cambiar significativamente si cambia la división de los datos.

## Posibles errores encontrados

Un posible error puede ocurrir si no se importa correctamente `SparkSession`:

```scala
import org.apache.spark.sql.SparkSession
```

Otro posible error puede ocurrir si no se importa `VectorAssembler`:

```scala
import org.apache.spark.ml.feature.VectorAssembler
```

También es necesario importar el clasificador:

```scala
import org.apache.spark.ml.classification.DecisionTreeClassifier
```

Y el evaluador:

```scala
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
```

Otro error común puede aparecer si se intenta entrenar el modelo sin tener una columna `features`. Los modelos de Spark ML esperan que las características estén en una sola columna vectorial.

Por eso, antes de entrenar el modelo, es necesario ejecutar:

```scala
val dataset = assembler.transform(data)
```

También se debe verificar que las columnas `label` y `features` existan en el DataFrame antes de llamar a:

```scala
dt.fit(trainingData)
```

## Conclusión

Se logró implementar correctamente un modelo de clasificación utilizando `DecisionTreeClassifier` en Apache Spark con Scala.

La práctica permitió comprender cómo construir un dataset manualmente, preparar las características mediante `VectorAssembler`, dividir los datos en entrenamiento y prueba, entrenar un árbol de decisión y evaluar su rendimiento mediante la métrica `accuracy`.

El algoritmo de árbol de decisión es útil porque permite generar modelos interpretables mediante reglas lógicas. Esto facilita entender por qué el modelo toma determinadas decisiones.

En este ejemplo, el modelo fue aplicado a un caso simple de aprobación de crédito, usando variables como ingresos altos, deuda e historial crediticio.

Aunque el dataset utilizado fue pequeño, la práctica permitió observar el flujo completo de un proceso de Machine Learning supervisado en Spark ML.

Esta práctica demuestra que Apache Spark puede utilizarse no solo para procesamiento de datos, sino también para construir, entrenar y evaluar modelos de Machine Learning de manera estructurada.