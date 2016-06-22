package com.duykien.usc.GIS.measure;

import java.util.ArrayList;

import com.duykien.usc.GIS.DP.LocationEntropyInfo;

public class LocationEntropyDPMeasureEntropyEvaluator {

	public static ArrayList<LocationEntropyInfo> convertEntropyToBucketValue(ArrayList<LocationEntropyInfo> leList,
			double bucketSize) {
		if (leList != null) {
			ArrayList<LocationEntropyInfo> convertedList = new ArrayList<>();

			for (int i = 0; i < leList.size(); i++) {
				LocationEntropyInfo info = leList.get(i);

				LocationEntropyInfo convertedInfo = new LocationEntropyInfo(info);
				convertedInfo.setEntropy(
						LocationEntropyDPMeasureHistogramGenerator.calBucketIndex(bucketSize, info.getEntropy()));
				convertedInfo.setPrivateEntropy(LocationEntropyDPMeasureHistogramGenerator.calBucketIndex(bucketSize,
						info.getPrivateEntropy()));

				convertedList.add(convertedInfo);
			}

			return convertedList;
		}
		return null;
	}

	public static ArrayList<LocationEntropyInfo> convertEntropyToProbabilities(ArrayList<LocationEntropyInfo> leList) {
		if (leList != null) {
			ArrayList<LocationEntropyInfo> convertedList = new ArrayList<>();

			double sumEntropy = 0;
			double sumPrivateEntropy = 0;

			for (int i = 0; i < leList.size(); i++) {
				LocationEntropyInfo info = leList.get(i);
				sumEntropy += info.getEntropy();
				sumPrivateEntropy += info.getPrivateEntropy();
			}

			for (int i = 0; i < leList.size(); i++) {
				LocationEntropyInfo info = leList.get(i);

				LocationEntropyInfo convertedInfo = new LocationEntropyInfo(info);
				convertedInfo.setEntropy(info.getEntropy() / sumEntropy);
				convertedInfo.setPrivateEntropy(info.getPrivateEntropy() / sumPrivateEntropy);

				convertedList.add(convertedInfo);
			}

			return convertedList;
		}
		return null;
	}

	public static MeasurementResults evaluateEntropy(ArrayList<LocationEntropyInfo> rawLocationEntropyList,
			ArrayList<LocationEntropyInfo> leDPList, double bucketSize) {
		MeasurementResults results = new MeasurementResults();
		// check size
		if (rawLocationEntropyList.size() != leDPList.size()) {
			System.out.println("Error evaluating entropy: size differs");
			return results;
		}

		// check if location ids matched
		for (int i = 0; i < leDPList.size(); i++) {
			LocationEntropyInfo rawLE = rawLocationEntropyList.get(i);
			LocationEntropyInfo dpLE = leDPList.get(i);
			if (rawLE.getLocationId() != dpLE.getLocationId()) {
				System.out.println("Error evaluating entropy: location id differs");
				return results;
			}
		}

		// convert to buckets
		ArrayList<LocationEntropyInfo> convertedOriginals = convertEntropyToBucketValue(rawLocationEntropyList,
				bucketSize);
		ArrayList<LocationEntropyInfo> convertedDP = convertEntropyToBucketValue(leDPList, bucketSize);
		
		int L = convertedOriginals.size();
		
		//MSE
		double[] actualEntropy = new double[L];
		double[] limitedEntropy = new double[L];
		double[] privateEntropy = new double[L];
		for (int i = 0; i < L; i++) {
			actualEntropy[i] = convertedOriginals.get(i).getEntropy();
			limitedEntropy[i] = convertedDP.get(i).getEntropy();
			privateEntropy[i] = convertedDP.get(i).getPrivateEntropy();
		}
		results.MSEPrivateVsActual = MSECalculator.calMSE(privateEntropy, actualEntropy);
		results.MSELimitedVsActual = MSECalculator.calMSE(limitedEntropy, actualEntropy);
		results.MSEPrivateVsLimited = MSECalculator.calMSE(privateEntropy, limitedEntropy);

		
		//KL-divergence and KS test
		convertedOriginals = convertEntropyToProbabilities(convertedOriginals);
		convertedDP = convertEntropyToProbabilities(convertedDP);


		// prob and cdf of originals
		double[] pActuals = new double[L];
		double[] cdfActuals = new double[L];
		double cdf = 0;
		for (int i = 0; i < L; i++) {
			LocationEntropyInfo info = convertedOriginals.get(i);
			pActuals[i] = info.getEntropy();
			cdf += pActuals[i];
			cdfActuals[i] = cdf;
		}

		// prob and cdf of limited, non-private version
		double[] pLimited = new double[L];
		double[] cdfLimited = new double[L];
		cdf = 0;
		for (int i = 0; i < L; i++) {
			LocationEntropyInfo info = convertedDP.get(i);
			pLimited[i] = info.getEntropy();
			cdf += pLimited[i];
			cdfLimited[i] = cdf;
		}

		// prob and cdf of limited, non-private version
		double[] pPrivate = new double[L];
		double[] cdfPrivate = new double[L];
		cdf = 0;
		for (int i = 0; i < L; i++) {
			LocationEntropyInfo info = convertedDP.get(i);
			pPrivate[i] = info.getPrivateEntropy();
			cdf += pPrivate[i];
			cdfPrivate[i] = cdf;
		}
		
		results.klDivergencePrivateVsActual = KLDivergenceCalculator.klDivergence(pPrivate, pActuals);
		results.ksTestValuePrivateVsActual = KSTestCalculator.calKolmogorovSmirnovTest(cdfPrivate, cdfActuals);
		
		results.klDivergenceLimitedVsActual = KLDivergenceCalculator.klDivergence(pLimited, pActuals);
		results.ksTestValueLimitedVsActual = KSTestCalculator.calKolmogorovSmirnovTest(cdfLimited, cdfActuals);
		
		results.klDivergencePrivateVsLimited = KLDivergenceCalculator.klDivergence(pPrivate, pLimited);
		results.ksTestValuePrivateVsLimited = KSTestCalculator.calKolmogorovSmirnovTest(cdfPrivate, cdfLimited);
		
		return results;
	}
}
