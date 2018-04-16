package com.albertortizl.katas.poker

import com.albertortizl.katas.poker.implicits._

class Showdown(parseLines: (String) => Either[String, Player] = Player.parse,
               toLine: (HandState) => String = HandState.toLine) {

  private val bestHand: List[Card] => Hand = Ranking.bestHand
  private val isWinner: (Hand, List[Hand]) => Boolean = Hand.isWinner

  def evaluate(lines: List[String]): Either[String, List[String]] = {

    lines.map(parseLines)
      .sequence
      .map(compareHands)
      .map(_ map toLine)

  }

  private def compareHands(players: List[Player]): List[HandState] = {

    val hands: List[HandState] = players.map(p => if (p.fold) Folded(p) else Finalist(p, bestHand(p.allCards)))

    hands.map {
      case finalist@Finalist(pc, hand) if isWinner(finalist.hand, opponents(finalist, hands)) => Winner(pc, hand)
      case finalist: Finalist => finalist
      case folded: Folded => folded
    }
  }

  private def opponents(finalist: Finalist, hands: List[HandState]): List[Hand] =
    hands.collect { case opponent: Finalist if opponent != finalist => opponent.hand }

}

