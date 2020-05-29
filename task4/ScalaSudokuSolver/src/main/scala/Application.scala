import java.util._

import scala.jdk.CollectionConverters._
import util.control.Breaks._
import scala.language.postfixOps

object Application {

  var possibleValuesForSudoku: List[List[Integer]] = new ArrayList()

  def main(args: Array[String]): Unit = {
    solve(Sudokus.boardEmpty)
  }



  def solve(board: Array[Array[Int]]): Unit = {
    var lastRun: Int = 0
    breakable {
      while (true) {
        possibleValuesForSudoku = evaluatePossibleSolutions(board)
        eliminateBySquare(board, possibleValuesForSudoku)
        val openValues: Int = updateSudokuField(board, possibleValuesForSudoku)
        if (openValues == lastRun) {
          val isSolved = solveSudoku(board, board.length)
          if (isSolved) {
            outputSudokuField(board)
            break()
          }
        }
        lastRun = openValues
      }
    }

  }


  def solveSudoku(board: Array[Array[Int]], n: Int): Boolean = {
    var row: Int = -1
    var col: Int = -1
    var isEmpty: Boolean = true
    breakable {
      for (i <- 0 until n) {
        breakable {
          // Check if we still have 0 values in our board
          for (j <- 0 until n if board(i)(j) == 0) {
            row = i
            col = j
            isEmpty = false
            break()
          }
        }
        // we still have some remaining
        if (!isEmpty) {
          break()
        }
      }
    }
    // no empty space left
    if (isEmpty) {
      return true
    }
    // else for each-row backtrack
    val index: Int = calculateIndexForPossibleValueField(row, col)
    for (i <- 0 until possibleValuesForSudoku.get(index).size) {
      val num: Int = possibleValuesForSudoku.get(index).get(i)
      if (validateCheckboxValue(board, row, col, num)) {
        board(row)(col) = num
        if (solveSudoku(board, n)) {
          true
        } else {
          board(row)(col) = 0
        }
      }
    }
    false
  }

  def calculateIndexForPossibleValueField(i: Int, j: Int): Int = {
    val fieldSize: Int = 9
    (i * fieldSize) + j
  }

  def validateCheckboxValue(board: Array[Array[Int]],
                            row: Int,
                            col: Int,
                            num: Int): Boolean = {
    for (d <- 0 until board.length if board(row)(d) == num) {
      return false
    }
    for (r <- 0 until board.length if board(r)(col) == num) {
      return false
    }
    // unique number (box-clash)
    val sqrt: Int = Math.sqrt(board.length).toInt
    val boxRowStart: Int = row - row % sqrt
    val boxColStart: Int = col - col % sqrt
    for (r <- boxRowStart until boxRowStart + sqrt;
         d <- boxColStart until boxColStart + sqrt if board(r)(d) == num) {
      return false
    }
    // if there is no clash, it's safe
    true
  }

  def outputSudokuField(sudokuField: Array[Array[Int]]): Unit = {
    for (i <- 0 until sudokuField.length) {
      for (j <- 0 until sudokuField.length) {
        System.out.print(sudokuField(i)(j) + ", ")
      }
      println()
    }
  }

  def evaluatePossibleSolutions(sudokuField: Array[Array[Int]]): List[List[Integer]] = {
    val possibleValues: List[List[Integer]] = new ArrayList[List[Integer]]()
    for (i <- 0 until sudokuField.length) {
      val notPossibleHorizontalList: List[Integer] = eliminateHorizontalValues(sudokuField, i)
      for (j <- 0 until sudokuField.length) {
        val possibleValuesForField: List[Integer] = new ArrayList[Integer]()
        val notPossibleVerticalList: List[Integer] =
          eliminateVerticalValues(sudokuField, j)
        val mergedList: List[Integer] =
          merge(notPossibleHorizontalList, notPossibleVerticalList)
        if (sudokuField(i)(j) == 0) {
          var k: Int = 1
          while (k <= 9) {
            if (!mergedList.contains(k)) {
              possibleValuesForField.add(k)
            }
            {
              k += 1;
              k - 1
            }
          }
          possibleValues.add(possibleValuesForField)
        } else {
          val tmp: List[Integer] = new ArrayList[Integer]()
          tmp.add(sudokuField(i)(j))
          possibleValues.add(tmp)
        }
      }
    }
    possibleValues
  }

  def eliminateHorizontalValues(sudokuField: Array[Array[Int]],
                                currentCounter: Int): List[Integer] = {
    val notPossibleValues: List[Integer] = new ArrayList[Integer]()
    for (j <- 0 until sudokuField.length
         if sudokuField(currentCounter)(j) != 0) {
      notPossibleValues.add(sudokuField(currentCounter)(j))
    }
    notPossibleValues
  }

  def eliminateVerticalValues(sudokuField: Array[Array[Int]],
                              currentCounter: Int): List[Integer] = {
    val notPossibleValues: List[Integer] = new ArrayList[Integer]()
    for (j <- 0 until sudokuField.length
         if sudokuField(j)(currentCounter) != 0) {
      notPossibleValues.add(sudokuField(j)(currentCounter))
    }
    notPossibleValues
  }

  def eliminateBySquare(sudokuField: Array[Array[Int]],
                        possibleValues: List[List[Integer]]): Unit = {
    var counter: Int = 0
    var i: Int = 0
    while (i <= sudokuField.length - 3) {
      var j: Int = 0
      while (j <= sudokuField.length - 3) {
        val notPossibleValues: List[Integer] = new ArrayList[Integer]()
        notPossibleValues.add(sudokuField(i)(j))
        notPossibleValues.add(sudokuField(i)(j + 1))
        notPossibleValues.add(sudokuField(i)(j + 2))
        notPossibleValues.add(sudokuField(i + 1)(j))
        notPossibleValues.add(sudokuField(i + 1)(j + 1))
        notPossibleValues.add(sudokuField(i + 1)(j + 2))
        notPossibleValues.add(sudokuField(i + 2)(j))
        notPossibleValues.add(sudokuField(i + 2)(j + 1))
        notPossibleValues.add(sudokuField(i + 2)(j + 2))
        // Remove duplicates from the list
        val listWithoutDuplicates: List[Integer] =
          new ArrayList[Integer](new HashSet(notPossibleValues))
        if (listWithoutDuplicates.indexOf(0) != -1) {
          listWithoutDuplicates.remove(listWithoutDuplicates.indexOf(0))
        }
        // Merge the list with our possible values for each field
        possibleValues.set(
          counter,
          remove(possibleValues.get(counter), listWithoutDuplicates))
        possibleValues.set(
          counter + 1,
          remove(possibleValues.get(counter + 1), listWithoutDuplicates))
        possibleValues.set(
          counter + 2,
          remove(possibleValues.get(counter + 2), listWithoutDuplicates))
        possibleValues.set(
          counter + 9,
          remove(possibleValues.get(counter + 9), listWithoutDuplicates))
        possibleValues.set(
          counter + 10,
          remove(possibleValues.get(counter + 10), listWithoutDuplicates))
        possibleValues.set(
          counter + 11,
          remove(possibleValues.get(counter + 11), listWithoutDuplicates))
        possibleValues.set(
          counter + 18,
          remove(possibleValues.get(counter + 18), listWithoutDuplicates))
        possibleValues.set(
          counter + 19,
          remove(possibleValues.get(counter + 19), listWithoutDuplicates))
        possibleValues.set(
          counter + 20,
          remove(possibleValues.get(counter + 20), listWithoutDuplicates))
        // Check if there's an unique number inside possible values for one square
        checkIfSquareHasUniqueValuesInPossibleValues(possibleValues, counter)
        counter += 3
        j += 3
      }
      counter += 18
      i += 3
    }
  }

  private def checkIfSquareHasUniqueValuesInPossibleValues(
                                                            possibleValues: List[List[Integer]],
                                                            counter: Int): Unit = {
    val allValuesOfSquare: List[Integer] = new ArrayList[Integer]()
    allValuesOfSquare.addAll(possibleValues.get(counter))
    allValuesOfSquare.addAll(possibleValues.get(counter + 1))
    allValuesOfSquare.addAll(possibleValues.get(counter + 2))
    allValuesOfSquare.addAll(possibleValues.get(counter + 9))
    allValuesOfSquare.addAll(possibleValues.get(counter + 10))
    allValuesOfSquare.addAll(possibleValues.get(counter + 11))
    allValuesOfSquare.addAll(possibleValues.get(counter + 18))
    allValuesOfSquare.addAll(possibleValues.get(counter + 19))
    allValuesOfSquare.addAll(possibleValues.get(counter + 20))
    // eliminate all non unique values of square
    val uniqueValues: List[Integer] =
      checkForUniqueValue(allValuesOfSquare, counter)
    useUniqueValues(uniqueValues, possibleValues, counter)
  }

  private def checkForUniqueValue(possibleValuesForSquare: List[Integer],
                                  counter: Int): List[Integer] = {
    val removedDuplicates: List[Integer] = new ArrayList[Integer]()
    for (j <- 1 to 9) {
      var occurrence: Int = 0
      for (i <- 0 until possibleValuesForSquare.size
           if possibleValuesForSquare.get(i) == j) {
        {
          occurrence += 1;
        }
      }
      if (occurrence < 2) {
        removedDuplicates.add(j)
      }
      occurrence = 0
    }
    removedDuplicates
  }

  private def useUniqueValues(uniqueValues: List[Integer],
                              possibleValues: List[List[Integer]],
                              counter: Int): Unit = {
    for (i <- 0 until uniqueValues.size) {
      val unique: Int = uniqueValues.get(i)
      val tmp: List[Integer] = new ArrayList[Integer]()
      tmp.add(unique)
      var j: Int = 0
      while (j <= 18) {
        if (possibleValues.get(counter + j).indexOf(unique) != -1) {
          possibleValues.set(counter + j, tmp)
        }
        if (possibleValues.get(counter + j + 1).indexOf(unique) !=
          -1) {
          possibleValues.set(counter + j + 1, tmp)
        }
        if (possibleValues.get(counter + j + 2).indexOf(unique) !=
          -1) {
          possibleValues.set(counter + j + 2, tmp)
        }
        j += 9
      }
    }
  }

  def updateSudokuField(sudokuField: Array[Array[Int]],
                        possibleValuesForSudoku: List[List[Integer]]): Int = {
    var listCounter: Int = 0
    var amountOfReplacedValues: Int = 0
    for (i <- 0 until sudokuField.length; j <- 0 until sudokuField.length) {
      if (possibleValuesForSudoku.get(listCounter).size == 1) {
        sudokuField(i)(j) = possibleValuesForSudoku.get(listCounter).get(0)
        amountOfReplacedValues += 1
      }
      {
        listCounter += 1;
        listCounter - 1
      }
    }
    amountOfReplacedValues - sudokuField.length
  }

  def merge(first: List[Integer], second: List[Integer]): List[Integer] = {
    val mergedList: List[Integer] = new ArrayList[Integer]()
    mergedList.addAll(first)
    for (tmp <- second.asScala if !mergedList.contains(tmp)) {
      mergedList.add(tmp)
    }
    mergedList
  }

  def remove(first: List[Integer], second: List[Integer]): List[Integer] = {
    val mergedList: List[Integer] = new ArrayList[Integer]()
    mergedList.addAll(first)
    if (mergedList.size > 1) {
      for (tmp <- second.asScala if mergedList.contains(tmp))
        mergedList.remove(mergedList.indexOf(tmp))

    }
    mergedList
  }

}

class Application {}
