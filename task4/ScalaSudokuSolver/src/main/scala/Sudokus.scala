object Sudokus {

  val board1: Array[Array[Int]] = Array(
    Array(0, 5, 8, 0, 0, 1, 0, 0, 0),
    Array(0, 0, 0, 6, 0, 2, 0, 9, 0),
    Array(0, 2, 0, 3, 4, 5, 0, 6, 0),
    Array(0, 8, 5, 0, 1, 0, 4, 0, 0),
    Array(2, 0, 0, 0, 7, 4, 0, 0, 3),
    Array(0, 0, 0, 0, 3, 6, 0, 1, 5),
    Array(0, 3, 1, 0, 5, 0, 0, 0, 4),
    Array(0, 0, 0, 0, 0, 0, 1, 0, 0),
    Array(4, 0, 0, 0, 2, 0, 7, 0, 0)
  )

  val board2: Array[Array[Int]] = Array(
    Array(0, 3, 0, 0, 0, 0, 5, 0, 0),
    Array(0, 4, 7, 0, 0, 2, 0, 9, 0),
    Array(2, 0, 5, 0, 0, 9, 0, 4, 0),
    Array(0, 5, 0, 2, 6, 0, 0, 0, 0),
    Array(3, 8, 2, 0, 0, 0, 7, 0, 0),
    Array(9, 0, 0, 8, 5, 0, 1, 0, 0),
    Array(0, 0, 0, 0, 2, 0, 0, 0, 1),
    Array(0, 0, 1, 4, 8, 5, 0, 0, 6),
    Array(4, 6, 3, 0, 0, 0, 0, 5, 8)
  )

  val board3: Array[Array[Int]] = Array(
    Array(3, 0, 0, 0, 8, 5, 0, 0, 1),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 2, 0, 1, 0, 0, 3, 5, 0),
    Array(5, 1, 0, 0, 0, 7, 0, 8, 3),
    Array(7, 0, 4, 0, 0, 0, 6, 0, 2),
    Array(9, 3, 0, 8, 0, 0, 0, 7, 5),
    Array(0, 4, 7, 0, 0, 1, 0, 2, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(2, 0, 0, 9, 7, 0, 0, 0, 6)
  )


  val board4: Array[Array[Int]] = Array(
    Array(3, 0, 6, 5, 0, 8, 4, 0, 0),
    Array(5, 2, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 8, 7, 0, 0, 0, 0, 3, 1),
    Array(0, 0, 3, 0, 1, 0, 0, 8, 0),
    Array(9, 0, 0, 8, 6, 3, 0, 0, 5),
    Array(0, 5, 0, 0, 9, 0, 6, 0, 0),
    Array(1, 3, 0, 0, 0, 0, 2, 5, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 7, 4),
    Array(0, 0, 5, 2, 0, 6, 3, 0, 0)
  )

  val board5: Array[Array[Int]] = Array(
    Array(8, 6, 0, 0, 2, 0, 0, 0, 0),
    Array(0, 0, 0, 7, 0, 0, 0, 5, 9),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 6, 0, 8, 0, 0),
    Array(0, 4, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 5, 3, 0, 0, 0, 0, 7),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 2, 0, 0, 0, 0, 6, 0, 0),
    Array(0, 0, 7, 5, 0, 9, 0, 0, 0)
  )

  val board6: Array[Array[Int]] = Array(
    Array(8, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 3, 6, 0, 0, 0, 0, 0),
    Array(0, 7, 0, 0, 9, 0, 2, 0, 0),
    Array(0, 5, 0, 0, 0, 7, 0, 0, 0),
    Array(0, 0, 0, 0, 4, 5, 7, 0, 0),
    Array(0, 0, 0, 1, 0, 0, 0, 3, 0),
    Array(0, 0, 1, 0, 0, 0, 0, 6, 8),
    Array(0, 0, 8, 5, 0, 0, 0, 1, 0),
    Array(0, 9, 0, 0, 0, 0, 4, 0, 0)
  )


  val boardEmpty: Array[Array[Int]] = Array(
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0, 0)
  )

}
