package topsisifs;

import exceptions.InvalidAlternativesException;
import exceptions.InvalidCriteriaException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;

public class TopsisIFS {

	private static TopsisIFS instance = null;

	private static MathContext mathContext = null;

	private Alternative[] alternatives;
	private int noCriterias;

	private IFSInitialValue[][] IFSMatrix;

	private PositiveIdealSolution[] positiveIdealSolutions;
	private NegativeIdealSolution[] negativeIdealSolutions;

	private BigDecimal[] uncertaintyWeightsPositive;
	private BigDecimal[] uncertaintyWeightsNegative;

	private ComputedAlternative[] computedAlternatives;

	private static boolean debug = true;


	private TopsisIFS() {
	}

	public static TopsisIFS getInstance() {
		if (instance == null) {
			instance = new TopsisIFS();
			mathContext = new MathContext(4);
		}
		return instance;
	}

	public static class ComputedAlternative {
		private final Alternative alternative;
		private final BigDecimal positiveDistance;
		private final BigDecimal negativeDistance;
		private final BigDecimal relativeCloseness;


		public ComputedAlternative(Alternative alternative, BigDecimal positiveDistance, BigDecimal negativeDistance) {
			this.alternative = alternative;
			this.positiveDistance = positiveDistance;
			this.negativeDistance = negativeDistance;
			this.relativeCloseness = BigDecimal.ONE.subtract(
					this.positiveDistance.divide((this.positiveDistance.add(this.negativeDistance)), mathContext));
		}

		public Alternative getAlternative() {
			return alternative;
		}

		public BigDecimal getRelativeCloseness() {
			return relativeCloseness;
		}
	}

	public ComputedAlternative[] computeResult(Alternative[] alternatives) {
		validateInput(alternatives);
		initData(alternatives);

		computeMatrix();
		computeIdealSolutions();
		computeUncertaintyWeights();
		computeEucleidianDistances();
		rankAlternatives();
		return this.computedAlternatives;
	}

	private void initData(Alternative[] alternatives) {
		this.alternatives = alternatives;
		this.noCriterias = alternatives[0].getValues().size();
		this.IFSMatrix = new IFSInitialValue[alternatives.length][noCriterias];
		this.positiveIdealSolutions = new PositiveIdealSolution[noCriterias];
		this.negativeIdealSolutions = new NegativeIdealSolution[noCriterias];
		this.uncertaintyWeightsNegative = new BigDecimal[noCriterias];
		this.uncertaintyWeightsPositive = new BigDecimal[noCriterias];
		this.computedAlternatives = new ComputedAlternative[alternatives.length];
	}

	private void validateInput(Alternative[] alternatives) {
		Objects.requireNonNull(alternatives);
		if (alternatives.length == 0) {
			throw new InvalidAlternativesException(InvalidAlternativesException.Causes.EMPTY);
		}
		int noCriteria = -1;
		for (int i = 0; i < alternatives.length; i++) {
			if (i == 0) {
				noCriteria = alternatives[i].getValues().size();
			} else {
				if (alternatives[i].getValues().size() != noCriteria) {
					throw new InvalidCriteriaException(InvalidCriteriaException.Causes.COUNT_INCONSISTENCY);
				}
			}
		}
	}

	private void computeMatrix() {
		for (int i = 0; i < alternatives.length; i++) {
			for (int j = 0; j < alternatives[i].getValues().size(); j++) {
				this.IFSMatrix[i][j] = alternatives[i].getValues().get(j);
			}
		}
		if (debug) {
			printIFSMatrix();
		}
	}

	private void computeIdealSolutions() {
		double maxPositive,minPositive;
		double maxNegative,minNegative;
		maxPositive = maxNegative = -0x00;
		minPositive = minNegative = -0x00;
		for (int i = 0; i < IFSMatrix[i].length; i++) {
			for (int j = 0; j < IFSMatrix.length; j++) {
				if (j == 0) {
					maxPositive = maxNegative = IFSMatrix[j][i].getμA();
					minPositive = minNegative = IFSMatrix[j][i].getvA();
				}
				if (IFSMatrix[j][i].getμA() >= maxPositive) {
					maxPositive = IFSMatrix[j][i].getμA();
				}
				if (IFSMatrix[j][i].getμA() <= maxNegative) {
					maxNegative = IFSMatrix[j][i].getμA();
				}
				if (IFSMatrix[j][i].getvA() <= minPositive) {
					minPositive = IFSMatrix[j][i].getvA();
				}
				if (IFSMatrix[j][i].getvA() >= minNegative) {
					minNegative = IFSMatrix[j][i].getvA();
				}
			}
			var positiveIdealSolution = new PositiveIdealSolution(
					alternatives[i].getValues().get(0).getCriteria(),
					maxPositive,
					minPositive);
			var negativeIdealSolution = new NegativeIdealSolution(
					alternatives[i].getValues().get(0).getCriteria(),
					maxNegative,
					minNegative
			);
			positiveIdealSolutions[i] = positiveIdealSolution;
			negativeIdealSolutions[i] = negativeIdealSolution;
		}
		if (debug) {
			printIdealSolutions();
		}
	}

	private void computeUncertaintyWeights() {
		for (int i = 0; i < noCriterias; i++) {
			uncertaintyWeightsPositive[i] = BigDecimal.ONE.subtract(BigDecimal.valueOf(positiveIdealSolutions[i].getμA()))
					.subtract(BigDecimal.valueOf(positiveIdealSolutions[i].getvA()));
			uncertaintyWeightsNegative[i] = BigDecimal.ONE.subtract(BigDecimal.valueOf(negativeIdealSolutions[i].getμA()))
					.subtract(BigDecimal.valueOf(negativeIdealSolutions[i].getvA()));
		}
		if (debug) {
			printUncertaintyWeights();
		}
	}

	private void computeEucleidianDistances() {

		for (int i = 0; i < alternatives.length; i++) {
			BigDecimal weightedSumPositive = BigDecimal.ZERO;
			BigDecimal weightedSumNegative = BigDecimal.ZERO;
			for (int j = 0; j < noCriterias; j++) {
				IFSInitialValue initialValue = alternatives[i].getValues().get(j);
				var criteriaWeight = BigDecimal.valueOf(initialValue.getCriteria().getWeight());
				var μAPositiveIdeal = BigDecimal.valueOf(positiveIdealSolutions[j].getμA());
				var μANegativeIdeal = BigDecimal.valueOf(negativeIdealSolutions[j].getμA());
				var vaPositiveIdeal = BigDecimal.valueOf(positiveIdealSolutions[j].getvA());
				var vaNegativeIdeal = BigDecimal.valueOf(negativeIdealSolutions[j].getvA());
				var uncertainityWeightPositive = uncertaintyWeightsPositive[j];
				var uncertainityWeightNegative = uncertaintyWeightsNegative[j];
				var μA = BigDecimal.valueOf(initialValue.getμA());
				var vA = BigDecimal.valueOf(initialValue.getvA());

				weightedSumPositive = weightedSumPositive.add(
					criteriaWeight.multiply(((μAPositiveIdeal.subtract(μA))
							.pow(2))
						.add((vaPositiveIdeal.subtract(vA))
							.pow(2))
						.add((uncertainityWeightPositive.subtract(BigDecimal.ONE.subtract(μA).subtract(vA)))
							.pow(2))));

				weightedSumNegative = weightedSumNegative.add(
						criteriaWeight.multiply(((μANegativeIdeal.subtract(μA))
							.pow(2))
						.add((vaNegativeIdeal.subtract(vA))
							.pow(2))
						.add((uncertainityWeightNegative.subtract(BigDecimal.ONE.subtract(μA).subtract(vA)))
							.pow(2))));
			}

			BigDecimal positiveEucleidianDistance = (BigDecimal.valueOf(0.50).multiply(weightedSumPositive)).sqrt(mathContext);
			BigDecimal negativeEucleidianDistance = (BigDecimal.valueOf(0.50).multiply(weightedSumNegative)).sqrt(mathContext);
			computedAlternatives[i] = new ComputedAlternative(alternatives[i], positiveEucleidianDistance,
					negativeEucleidianDistance);
		}
		if (debug) {
			printEucleidianDistances();
			printComputedAlternatives();
		}
	}

	private void rankAlternatives() {
		this.computedAlternatives = Arrays.stream(this.computedAlternatives)
				.sorted(Comparator.comparing(ComputedAlternative::getRelativeCloseness).reversed())
				.toArray(ComputedAlternative[]::new);
		if (debug) {
			System.out.println("Rank:");
			printComputedAlternatives();
		}
	}


	private void printIFSMatrix() {
		System.out.println("\n");
		System.out.println("Matricea alternativa-criteriu");
		for (int i = 0; i < IFSMatrix.length; i++) {
			System.out.print("A" + (i + 1) + "={");
			for (int j = 0; j < IFSMatrix[i].length; j++) {
				System.out.print("(C" + (j + 1) + "," +
						IFSMatrix[i][j].getμA() + "," + IFSMatrix[i][j].getvA() + ")");
				if (j != IFSMatrix[i].length - 1) {
					System.out.print(",");
				} else {
					System.out.print("}\n");
				}
			}
		}
	}

	private void printIdealSolutions() {
		Consumer<IFSValue[]> idealSolutionsPrinter = (matrix) -> {
			for (int i = 0; i < matrix.length; i++) {
				System.out.print("(C" + (i + 1) + "," +
						matrix[i].getμA() + "," + matrix[i].getvA() + ")");
				if (i != matrix.length - 1) {
					System.out.print(",");
				} else {
					System.out.print("}\n");
				}
			}
		};

		System.out.println("\n");
		System.out.println("Soluția ideală pozitiva IFS A+");
		idealSolutionsPrinter.accept(positiveIdealSolutions);
		System.out.println("Soluția ideală potrivită IFS A-");
		idealSolutionsPrinter.accept(negativeIdealSolutions);
	}

	private void printUncertaintyWeights() {
		System.out.println("\n ");
		System.out.println("Gradul de nedeterminare");
		System.out.print("πA+ = ");
		System.out.println(Arrays.toString(uncertaintyWeightsPositive));
		System.out.print("πA- = ");
		System.out.print(Arrays.toString(uncertaintyWeightsNegative));
	}

	private void printEucleidianDistances() {
		System.out.println("\n");
		for (ComputedAlternative computedAlternative : computedAlternatives) {
			System.out.print("d(A+," + computedAlternative.alternative
					.getName() + ") = " + computedAlternative.positiveDistance);
			System.out.println("     d(A-," + computedAlternative.alternative
					.getName() + ") = " + computedAlternative.negativeDistance);
		}
	}

	private void printComputedAlternatives() {
		System.out.println("\n");
		for (ComputedAlternative computedAlternative : computedAlternatives) {
			System.out.print(computedAlternative.alternative.getName() + " = " + computedAlternative.relativeCloseness + "; ");
		}
	}
}
