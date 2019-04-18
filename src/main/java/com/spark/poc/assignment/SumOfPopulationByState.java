package com.spark.poc.assignment;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class SumOfPopulationByState {
	
public static void main(String[] args) {
		
		Logger.getLogger("org").setLevel(Level.ERROR);
		SparkConf conf = new SparkConf().setAppName("GCCSACountByState").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> ausStateDataSet = sc.textFile("in/AUS_state.csv");
		JavaRDD<String> filteredStateDataSet = ausStateDataSet.filter(line -> 
						!(line.contains("Rank")||line.contains("Population")||line.contains("national")));
		
		JavaPairRDD<String, Long> statePairDataSet = filteredStateDataSet.mapToPair(
															(PairFunction<String, String, Long>)line ->
																	new Tuple2<>(line.split(",")[2],
																			new Long(line.split(",")[3])));		
		JavaPairRDD<String, Long> singleStatePairDataSet = statePairDataSet.reduceByKey(
														(Function2<Long, Long, Long>) (x, y) -> x + y);
		
		Map<String, Long> stateDataMap = singleStatePairDataSet.collectAsMap();
		for(Map.Entry<String, Long> entry : stateDataMap.entrySet()) {
			System.out.println(entry.getKey()+":::"+entry.getValue());
		}	
	}

}
