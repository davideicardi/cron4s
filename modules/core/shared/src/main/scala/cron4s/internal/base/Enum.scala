/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s.internal
package base

import scala.collection.SortedSet

private[cron4s] trait Enum[A] {
  def members: SortedSet[A]
}

private[cron4s] object Enum {
  def apply[A](implicit ev: Enum[A]): Enum[A] = ev

  def fromSet[A](elems: SortedSet[A]): Enum[A] = new Enum[A] {
    val members: SortedSet[A] = elems
  }
}