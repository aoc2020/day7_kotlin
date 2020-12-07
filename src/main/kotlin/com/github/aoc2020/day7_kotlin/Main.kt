@file:JvmName("Main")

package com.github.aoc2020.day7_kotlin

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

fun parse_bag(s: String): Pair<Int, String> {
    println("parse_bag($s)");
    if (s.matches("^[0-9]+.*".toRegex())) {
        val i = s.substring(0, s.indexOf(' ')).trim().toInt()
        val v = s.substring(s.indexOf(' '), s.length).trim()
            .replace("bags", "bag");
        println("parse_bag#1: $i $v")
        return Pair(i, v)
    } else {
        println("parse_bag#2: !1 $s")
        return Pair(1, s)
    }
}

fun contains(s: String, b: List<Pair<Int, String>>): Boolean {
    var keys = b.stream().map { p -> p.second }.collect(Collectors.toSet());
//    println("Checking $s $keys => ${keys.contains(s)}")
    return keys.contains(s);
}

fun count_gold(s: List<Pair<String, List<Pair<Int, String>>>>): Int {
    val bags = s.stream().map { b -> b.first }.collect(Collectors.toSet());
    var set = s.stream()
        .filter { v -> contains("shiny gold bag", v.second) }
        .map { s -> s.first }
        .collect(Collectors.toSet());
    var i = 0;
    while (i != set.size) {
        i = set.size;
        val x:HashSet<String> = HashSet();
        x.addAll(set);
        for (curr in x) {
            var new = s.stream()
                .filter { v -> contains(curr, v.second) }
                .map { v -> v.first }
                .collect(Collectors.toSet());
//            println("Found new: $new")
            set.addAll(new);
        }
    }
    return set.size;
}

fun splitLine(si: String): Pair<String, List<Pair<Int, String>>> {
    // println(s)
    var s = si
        .replace("bags", "bag")
    val i = s.indexOf("contain");
    val bag = s.substring(0, i - 1);
    if (s.contains("no other bag")) {
        return Pair(bag,ArrayList())
    } else {
        val bags = s.substring(i + 8, s.length - 1).split(",").map { s -> s.trim() }
            .map { s -> parse_bag(s) }
        //    println("[$bag] -> [$bags]")
        return Pair(bag, bags)
    }

}

fun sum(l: List<Int>) : Int {
    return l.sum();
}

fun count(bag: Pair<Int,String>, bagSpecs: List<Pair<String,List<Pair<Int,String>>>>) : Int {
    return bag.first * requires_bags(bag.second, bagSpecs);
}

fun content_value(bags: List<Pair<Int,String>>, bagSpecs: List<Pair<String,List<Pair<Int,String>>>>): Int {
    val counts = bags.map { count_and_bag -> count( count_and_bag, bagSpecs) }
    if (counts.isNotEmpty()) println("ZIP ${counts.zip(bags)}");
    return sum(counts) + 1;
}

fun requires_bags(bag:String, bags: List<Pair<String, List<Pair<Int, String>>>>): Int {
    val content = bags.stream()
        .filter { s -> s.first.equals(bag) }
        .map { s -> s.second }
        .collect(Collectors.toList()).flatten();
    var sum = content_value(content, bags);
    if (sum == 1) {
        // println("$bag is empty");
    } else {
        println("$bag : $content = $sum")
    }
    return sum;
}

fun main() {
    try {
        val input = readFile("input.txt")
        val baglist = input.map { s -> splitLine(s) }
        var x = count_gold(baglist);
        println("Answer 1: $x");
        println("$baglist")
        var r = requires_bags("shiny gold bag", baglist);
        println("Answer 2: $r");
    } catch (e: Throwable) {
        println("Failed with exception $e")
        e.printStackTrace(System.out);
    }
}

fun readFile(fileName: String): List<String> {
    return Files.lines(Path.of(fileName))
        .collect(Collectors.toUnmodifiableList())
}