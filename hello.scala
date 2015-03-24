#!/bin/sh
exec scala "$0" "$@"
!#

/**
 * Just playing around...
 */

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set
import scala.collection.mutable.Map

val data = List(List("A", "B", "C"), List("A", "B", "E"),  List("X", "B", "E"),  List("A", "B", "E"),  List("A", "Z", "E"), List("A", "B", "E"))
val minSupp = 0.5


def frequentItemSets( dataSet:List[List[String]] ) : List[_] = {

    // Make a pass over the dataSet
    var frequentItemSets = List[Set[_]]()
    var frontier = List[Set[String]](Set())
    while (!frontier.isEmpty) {
        var candidateSet = ListBuffer[Set[String]]()
        var candidateSetCounts = Map[String, Int]()
        for (tuple <- dataSet) {
            var mutableTuple = tuple.to[ListBuffer]
            for (itemSet <- frontier) {
                if (tupleContainsAllItems(tuple, itemSet)) { //t contains all items of  itemset
                    // candidate itemsets that are extensions of itemSet and contained in tuple

                    /*println(" ============================ CANDIDATES =======================================")
                    println("- DB tuple")
                    println("  " + tuple)
                    println("--- Previous itemSet")
                    println("    " +itemSet)
                    println("----- Candidates")*/

                    var candidates = generateCandidates(itemSet, mutableTuple)
                    mutableTuple --= itemSet

                    //println("      " + candidates)

                    for (itemSet <- candidates) {
                        if (itemSet.size > 0) {
                            if (candidateSet.contains(itemSet)) {
                                candidateSetCounts(itemSet.mkString) = candidateSetCounts(itemSet.mkString) + 1
                            } else {
                                candidateSetCounts += itemSet.mkString -> 1
                                candidateSet += itemSet
                            }
                        }
                    }

                    /*println("----- Candidates count")
                    println("      " + candidateSetCounts)*/
                }
            }

        }

        // consolidate
        frontier = List[Set[String]]()
        for (itemSet <- candidateSet) {
            if (candidateSetCounts(itemSet.mkString).toFloat / dataSet.length >= minSupp) {
                frequentItemSets = itemSet :: frequentItemSets
                frontier = itemSet :: frontier
            }
        }

        /*println(" ============================ FRONTIER =======================================")
        println(frontier)*/
    }


    return frequentItemSets
}

def tupleContainsAllItems(tuple:List[String], itemSet:Set[_]) : Boolean = {
    return itemSet.forall(x => (x == "" || tuple.contains(x)));
}


def generateCandidates(itemSet:Set[String], tuple:ListBuffer[String]) : ListBuffer[Set[String]] = {

    var candidates = ListBuffer[Set[String]]()
    for (item <- tuple) {
        if (!itemSet.contains(item)) {
            var candidateSet = itemSet ++ Set(item)
            if (!candidates.contains(candidateSet)) {
                candidates += candidateSet
            }
        }
    }
    return candidates
}




object HelloWorld extends App {
  println("Hello, data!")
  println(data)
  println("Result!")
  println(frequentItemSets(data))
}


HelloWorld.main(args)