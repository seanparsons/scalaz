package scalaz
package std

import std.AllInstances._
import scalaz.scalacheck.ScalazProperties._
import scala.math.{Ordering => SOrdering}
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._

class MapTest extends Spec {
  checkAll(traverse.laws[({type F[V] = Map[Int,V]})#F])
  checkAll(isEmpty.laws[({type F[V] = Map[Int,V]})#F])
  checkAll(monoid.laws[Map[Int,String]])
  checkAll(order.laws[Map[Int,String]])
  
  "map inequality" ! forAllNoShrink(arbitrary[Map[String, Int]].filter(_.size > 0)){map =>
    val modifiedMap = map - map.head._1
    !Equal[Map[String, Int]].equal(modifiedMap, map)
  }

  "map equality" ! prop{(list: List[(String, Int)]) =>
    val map1 = list.toMap
    val map2 = list.toMap
    Equal[Map[String, Int]].equal(map1, map2)
  }

  "map ordering" ! prop {
    val O = implicitly[Order[Map[String,Int]]]
    val O2 = SOrdering.Iterable(implicitly[SOrdering[(String,Int)]])
    (kvs: List[(String,Int)], kvs2: List[(String,Int)]) => {
      val (m1, m2) = (kvs.toMap, kvs2.toMap)
      ((m1.size == kvs.size) && (m2.size == kvs2.size)) ==> {
        val l: Boolean = O.lessThan(m1, m2)
        val r: Boolean = (if (m1.size < m2.size) true
                          else if (m1.size > m2.size) false
                          else O2.lt(kvs.sortBy(_._1), kvs2.sortBy(_._1)))
        l == r
      }
    }
  }
}
