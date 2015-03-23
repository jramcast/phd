#!/bin/sh
exec scala "$0" "$@"
!#

val data = List(List("A", "B", "C"), List("A", "B", "E"))
val minSupp = 0.5


def frequentItemSets( dataSet:List[List[String]] ) : List[_] = {

    // Make a pass over the dataSet
    var frequentItemSets = List()
    var frontier = List(List())
    while (!frontier.isEmpty) {
        var candidateSet = List()
        for (t <- dataSet) {
            for (f <- frontier) {
                if (t.contains(f)) {
                    // candidate itemsets that are extensions of f and contained in t
                    // For all itemsets....
                }

                    // consolidate
                    frontier = List()
                    for (c <- candidateSet) {
                        if (c.length / dataSet.length > minSupp) {
                            frequentItemSets = c :: frequentItemSets
                        }
                    }

            }
        }
    }


    return frequentItemSets
}


object HelloWorld extends App {
  println("Hello, data!")
  println(data)
  println(frequentItemSets(data))
}


HelloWorld.main(args)