package algo;

import exceptions.InvalidAlternativesException;
import exceptions.InvalidCriteriaException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class Topsis {

	private static Topsis instance = null;

	private Alternative[] alternatives;
	private int noCriterias;

	private IFSInitialValue[][] IFSMatrix;

	private PositiveIdealSolution[] positiveIdealSolutions;
	private NegativeIdealSolution[] negativeIdealSolutions;

	private BigDecimal[] uncertaintyWeightsPositive;
	private BigDecimal[] uncertaintyWeightsNegative;

	private EucleidianDistance[] eucleidianDistances;
	private OutputCandidate[] outputCandidates;

	private boolean debug = true;


	private Topsis() {
	}

	public static Topsis getInstance() {
		if (instance == null) {
			instance = new Topsis();
		}
		return instance;
	}

	private class EucleidianDistance {
		private Alternative alternative;
		private BigDecimal positiveDistance;
		private BigDecimal negativeDistance;

		public EucleidianDistance(Alternative alternative, BigDecimal positiveDistance, BigDecimal negativeDistance) {
			this.alternative = alternative;
			this.positiveDistance = positiveDistance;
			this.negativeDistance = negativeDistance;
		}
	}

	private class OutputCandidate {
		private Alternative alternative;
		private BigDecimal relativeCloseness;
	}

	public OutputCandidate[] computeResult(Alternative[] alternatives) {
		validateInput(alternatives);
		initData(alternatives);

		computeMatrix();
		computeIdealSolutions();
		computeUncertaintyWeights();
		computeEucleidianDistances();
		return this.outputCandidates;
	}

	private void initData(Alternative[] alternatives) {
		this.alternatives = alternatives;
		this.noCriterias = alternatives[0].getValues().size();
		this.IFSMatrix = new IFSInitialValue[alternatives.length][noCriterias];
		this.positiveIdealSolutions = new PositiveIdealSolution[noCriterias];
		this.negativeIdealSolutions = new NegativeIdealSolution[noCriterias];
		this.uncertaintyWeightsNegative = new BigDecimal[noCriterias];
		this.uncertaintyWeightsPositive = new BigDecimal[noCriterias];
		this.eucleidianDistances = new EucleidianDistance[alternatives.length];
		this.outputCandidates = new OutputCandidate[alternatives.length];
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

		var mathContext = new MathContext(4);

		for (int i = 0; i < alternatives.length; i++) {
			BigDecimal weightedSum = BigDecimal.ZERO;
			for (int j = 0; j < noCriterias; j++) {
				IFSInitialValue initialValue = alternatives[i].getValues().get(j);
				var criteriaWeight = BigDecimal.valueOf(initialValue.getCriteria().getWeight());
				var μAPositiveIdeal = BigDecimal.valueOf(positiveIdealSolutions[j].getμA());
				var vaPositiveIdeal = BigDecimal.valueOf(positiveIdealSolutions[j].getvA());
				var uncertainityWeightPositive = uncertaintyWeightsPositive[j];
				var μA = BigDecimal.valueOf(initialValue.getμA());
				var vA = BigDecimal.valueOf(initialValue.getvA());

				weightedSum = weightedSum.add(
					criteriaWeight.multiply(((μAPositiveIdeal.subtract(μA))
							.pow(2))
						.add((vaPositiveIdeal.subtract(vA))
							.pow(2))
						.add((uncertainityWeightPositive.subtract(BigDecimal.ONE.subtract(μA).subtract(vA)))
							.pow(2))));
			}
			BigDecimal positiveEucleidianDistance = (BigDecimal.valueOf(0.50).multiply(weightedSum)).sqrt(mathContext);
			eucleidianDistances[i] = new EucleidianDistance(alternatives[i], positiveEucleidianDistance,
					BigDecimal.ZERO);
		}
		if (debug) {
			printEucleidianDistances();
		}
	}

	public void printIFSMatrix() {
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

	public void printIdealSolutions() {
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

	public void printUncertaintyWeights() {
		System.out.println("\n ");
		System.out.println("Gradul de nedeterminare");
		System.out.print("πA+ = ");
		System.out.println(Arrays.toString(uncertaintyWeightsPositive));
		System.out.print("πA- = ");
		System.out.print(Arrays.toString(uncertaintyWeightsNegative));
	}

	public void printEucleidianDistances() {
		System.out.println("\n");
		for (int i = 0; i < eucleidianDistances.length; i++) {
			System.out.print("d(A+," + eucleidianDistances[i].alternative.getName()
					+ ") = " + eucleidianDistances[i].positiveDistance);
			System.out.println("     d(A-," + eucleidianDistances[i].alternative.getName()
					+ ") = " + eucleidianDistances[i].negativeDistance);
		}
	}
}
