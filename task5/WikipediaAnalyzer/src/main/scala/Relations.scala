import org.apache.spark.mllib.linalg.{DenseMatrix, DenseVector, Matrices, Matrix, SingularValueDecomposition, Vector}
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix, RowMatrix}
import org.apache.spark.rdd.RDD

class Relations( val svd: SingularValueDecomposition[RowMatrix, Matrix],
                 val wordToIndex: Map[String, Int],
                 val indexToWord: Array[String],
                 val docToIndex: scala.collection.Map[String, Long]
               ) {

  val totalWords = wordToIndex.size
  val totalDocs = docToIndex.size
  val indexToDoc = docToIndex.map(pair => (pair._2, pair._1))

  val vs = svd.V.multiply(DenseMatrix.diag(svd.s)).transpose
  val vsRowMatrix = new RowMatrix(matrixToRDD(vs))
  val termTermRelation = vsRowMatrix.columnSimilarities().toBlockMatrix().toLocalMatrix()

  val us = svd.U.multiply(DenseMatrix.diag(svd.s))
  val usTransposed = transposeRowMatrix(us)
  val docDocRelation = usTransposed.columnSimilarities().toBlockMatrix().toLocalMatrix()

  //Returns the n terms that are most related to the given term
  def termTerms(term: String, n: Int): Array[String] = {
    val ind = wordToIndex(term)
    val relatedWords = new Array[Double](totalWords)
    for(i <- 0 to (totalWords - 1))
      relatedWords(i) = termTermRelation(ind, i)
    val sorted = relatedWords.zipWithIndex.sortBy(_._1)(Ordering[Double].reverse)
    val mostRelatedTerms = sorted.slice(0, n - 1).map(pair => indexToWord(pair._2))
    return mostRelatedTerms
  }

  //Returns the n docs that are most related to the given doc
  def docDocs(doc: String, n: Int): Array[String] = {
    val ind = docToIndex(doc).toInt
    val relatedDocs = new Array[Double](totalDocs)
    for(i <- 0 to (totalDocs - 1))
      relatedDocs(i) = docDocRelation(ind, i)
    val sorted = relatedDocs.zipWithIndex.sortBy(_._1)(Ordering[Double].reverse)
    val mostRelatedDocs = sorted.slice(0, n - 1).map(pair => indexToDoc(pair._2))
    return mostRelatedDocs
  }

  //Returns the n docs that are most related to the given term
  def termDocs(term: String, n: Int): Array[String] = {
    val ind = wordToIndex(term)
    val row = new Array[Double](svd.V.numCols)
    for(i <- 0 to (svd.V.numCols - 1))
      row(i) = svd.V(ind, i)
    val transposedRow = Matrices.dense(svd.V.numCols, 1, row)
    val termDocRelation = us.multiply(transposedRow).rows.collect().map(row => row(0))
    val sorted = termDocRelation.zipWithIndex.sortBy(_._1)(Ordering[Double].reverse)
    val mostRelatedDocs = sorted.slice(0, n - 1).map(pair => indexToDoc(pair._2))
    return mostRelatedDocs
  }

  //Source: https://stackoverflow.com/questions/30169841/convert-matrix-to-rowmatrix-in-apache-spark-using-scala
  private def matrixToRDD(m: Matrix): RDD[Vector] = {
    val columns = m.toArray.grouped(m.numRows)
    val rows = columns.toSeq.transpose // Skip this if you want a column-major RDD.
    val vectors = rows.map(row => new DenseVector(row.toArray))
    WikipediaAnalyzer.spark.sparkContext.parallelize(vectors)
  }

  //Source: https://stackoverflow.com/a/43158526
  private def transposeRowMatrix(m: RowMatrix): RowMatrix = {
    val indexedRM = new IndexedRowMatrix(m.rows.zipWithIndex.map({
      case (row, idx) => new IndexedRow(idx, row)}))
    val transposed = indexedRM.toCoordinateMatrix().transpose.toIndexedRowMatrix()
    new RowMatrix(transposed.rows
      .map(idxRow => (idxRow.index, idxRow.vector))
      .sortByKey().map(_._2))
  }
}