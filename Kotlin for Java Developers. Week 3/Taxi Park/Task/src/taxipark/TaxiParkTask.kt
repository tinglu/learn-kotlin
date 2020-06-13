package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
    allDrivers.subtract(trips.map { it.driver })

fun TaxiPark.findFakeDrivers2(): Set<Driver> =
    allDrivers.minus(trips.map { it.driver })

fun TaxiPark.findFakeDrivers3(): Set<Driver> =
    allDrivers.filter { d -> trips.none { it.driver == d } }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> {
    if (minTrips == 0) {
        return allPassengers
    } else {
        return trips
            .flatMap { it.passengers }
            .groupingBy { it }
            .eachCount()
            .filter { it.value >= minTrips }
            .keys
    }
}

// Better solution: (don't need to handle minTrips == 0)
fun TaxiPark.findFaithfulPassengers2(minTrips: Int): Set<Passenger> =
    allPassengers
        .filter { p ->
            trips.count { p in it.passengers } >= minTrips
        }
        .toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
    trips
        .filter { it.driver.name == driver.name }
        .flatMap { it.passengers }
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 } // more than once
        .keys

// Similar to mine
fun TaxiPark.findFrequentPassengers2(driver: Driver): Set<Passenger> =
    trips
        .filter { it.driver == driver }
        .flatMap { it.passengers }
        .groupBy { p -> p }
        .filterValues { group -> group.size > 1 } // use filterValues here
        .keys

fun TaxiPark.findFrequentPassengers3(driver: Driver): Set<Passenger> =
    allPassengers
        .filter { p ->
            trips.count { it.driver == driver && p in it.passengers } > 1
        }
        .toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val passengerTrips =
        trips.flatMap { it.passengers }
            .groupingBy { it }
            .eachCount()
    val passengerSmartTrips =
        trips.filter { it.discount != null && it.discount > 0.0 }
            .flatMap { it.passengers }
            .groupingBy { it }
            .eachCount()
    return passengerSmartTrips
        .filter { it.value * 2 > passengerTrips.getOrDefault(it.key, 0) }
        .keys
}

// Better solution:
fun TaxiPark.findSmartPassengers2(): Set<Passenger> {
    val (tripsWithDiscount, tripsWithoutDiscout) =
        trips.partition { it.discount != null }
    return allPassengers
        .filter { passenger ->
            tripsWithDiscount.count { passenger in it.passengers } >
                tripsWithoutDiscout.count { passenger in it.passengers }
        }
        .toSet()
}

// I don't like this solution:
fun TaxiPark.findSmartPassengers3(): Set<Passenger> =
    allPassengers
        .associate { p ->
            p to trips.filter { t -> p in t.passengers }
        }
        .filterValues {
            val (withDiscount, withoutDiscount) =
                it.partition { it.discount != null }
            withDiscount.size > withoutDiscount.size
        }
        .keys

// Maybe:
fun TaxiPark.findSmartPassengers4(): Set<Passenger> =
    allPassengers.filter { p ->
        val withDiscount = trips.count { t -> p in t.passengers && t.discount != null }
        val withoutDiscount = trips.count { t -> p in t.passengers && t.discount == null }
        withDiscount > withoutDiscount
    }.toSet()

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val intervalToOccurrence =
        trips.map { it.duration - it.duration % 10 } // map to it's closest left (start) of its range
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { (_, counting) -> counting }
    if (trips.isEmpty()) {
        return null
    } else {
        val start = intervalToOccurrence.first().first
        val end = start + 9
        return start..end
    }
}

// Similar to mine but cleaner
fun TaxiPark.findTheMostFrequentTripDurationPeriod2(): IntRange? {
    return trips
        .groupBy {
            val start = it.duration / 10 * 10
            val end = start + 9
            start..end
        }
        // .toList()
        // .maxBy { (_, group) -> group.size }
        // ?.first
        .maxBy { (_, group) -> group.size } // can call maxBy on a map directly
        ?.key
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    val totalIncome = trips.sumByDouble { it.cost }
    val incomeThreshold = totalIncome * 0.8
    val groupByDriver =
        trips.groupingBy { it.driver }
            .fold(0.0) { total, trip -> total + trip.cost }
            .toList()
            .sortedByDescending { (_, value) -> value }

    var runningTotal = 0.0
    var count = 0
    for (driverEarnings: Pair<Driver, Double> in groupByDriver) {
        runningTotal += driverEarnings.second
        count++
        if (runningTotal >= incomeThreshold) {
            break
        }
    }

    return trips.isNotEmpty() && count <= allDrivers.size * 0.2
}

// Similar but better solution:
fun TaxiPark.checkParetoPrinciple2(): Boolean {
    if (trips.isEmpty()) return false

    val totalIncome = trips.sumByDouble { it.cost }
    val sortedDriversIncome: List<Double> = trips
        .groupBy { it.driver }
        .map { (_, tripsByDriver) -> tripsByDriver.sumByDouble { it.cost } }
        .sortedDescending()

    val numberOfTopDrivers = (0.2 * allDrivers.size).toInt()
    val incomeByTopDrivers = sortedDriversIncome
        .take(numberOfTopDrivers)
        .sum()

    return incomeByTopDrivers >= 0.8 * totalIncome
}
