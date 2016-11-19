import java.util.HashMap;
import java.util.Map;

/**
 * The Damerau-Levenshtein Algorithm is an extension to the Levenshtein
 * Algorithm which solves the edit distance problem between a source string and
 * a target string with the following operations:
 * 
 * <ul>
 * <li>Character Insertion</li>
 * <li>Character Deletion</li>
 * <li>Character Replacement</li>
 * <li>Adjacent Character Swap</li>
 * </ul>
 * 
 * Note that the adjacent character swap operation is an edit that may be
 * applied when two adjacent characters in the source string match two adjacent
 * characters in the target string, but in reverse order, rather than a general
 * allowance for adjacent character swaps.
 * <p>
 * 
 * This implementation allows the client to specify the costs of the various
 * edit operations with the restriction that the cost of two swap operations
 * must not be less than the cost of a delete operation followed by an insert
 * operation. This restriction is required to preclude two swaps involving the
 * same character being required for optimality which, in turn, enables a fast
 * dynamic programming solution.
 * <p>
 * 
 * The running time of the Damerau-Levenshtein algorithm is O(n*m) where n is
 * the length of the source string and m is the length of the target string.
 * This implementation consumes O(n*m) space.
 * 
 * @author Kevin L. Stern
 */
public class DamerauLevenshtein {
  private final int custoRemocao, custoInsercao, custoSubstituicao, custoTroca;

  /**
   * Constructor.
   * 
   * @param custoRemocao
   *          the cost of deleting a character.
   * @param insertCost
   *          the cost of inserting a character.
   * @param replaceCost
   *          the cost of replacing a character.
   * @param swapCost
   *          the cost of swapping two adjacent characters.
   */
  public DamerauLevenshtein(int custoRemocao, int custoInsercao,
                                     int custoSubstituicao, int custoTroca) {
    /*
     * Required to facilitate the premise to the algorithm that two swaps of the
     * same character are never required for optimality.
     */
    if (2 * custoTroca < custoInsercao + custoRemocao) {
      throw new IllegalArgumentException("Unsupported cost assignment");
    }
    this.custoRemocao = custoRemocao;
    this.custoInsercao = custoInsercao;
    this.custoSubstituicao = custoSubstituicao;
    this.custoTroca = custoTroca;
  }

  /**
   * Compute the Damerau-Levenshtein distance between the specified source
   * string and the specified target string.
   */
  // a ordem entre as strings importa. calcula a distância da primeira para a segunda
  public int calcularDistancia (String primeiraString, String segundaString) {
	 
	//considera que todos os caracteres foram inseridos  
    if (primeiraString.length() == 0) {
      return segundaString.length() * custoInsercao;
    }
    //considera que todos os caracteres foram removidos
    if (segundaString.length() == 0) {
      return primeiraString.length() * custoRemocao;
    }
    
    int[][] matrizDeDistancias = new int[primeiraString.length()][segundaString.length()];
    
    Map<Character, Integer> indicesDaPrimeiraStringPorCaracter = new HashMap<Character, Integer>();
    
    if (primeiraString.charAt(0) != segundaString.charAt(0)) {
      matrizDeDistancias[0][0] = Math.min(custoSubstituicao, custoRemocao + custoInsercao);
    }
    
    indicesDaPrimeiraStringPorCaracter.put(primeiraString.charAt(0), 0);
    
    for (int i = 1; i < primeiraString.length(); i++) {
      int distanciaRemocao = matrizDeDistancias[i - 1][0] + custoRemocao;
      int distanciaInsercao = (i + 1) * custoRemocao + custoInsercao;
      int distanciaSubstituicao = i * custoRemocao
          + (primeiraString.charAt(i) == segundaString.charAt(0) ? 0 : custoSubstituicao);
      matrizDeDistancias[i][0] = Math.min(Math.min(distanciaRemocao, distanciaInsercao),
                             distanciaSubstituicao);
    }
    for (int j = 1; j < segundaString.length(); j++) {
      int deleteDistance = (j + 1) * custoInsercao + custoRemocao;
      int insertDistance = matrizDeDistancias[0][j - 1] + custoInsercao;
      int matchDistance = j * custoInsercao
          + (primeiraString.charAt(0) == segundaString.charAt(j) ? 0 : custoSubstituicao);
      matrizDeDistancias[0][j] = Math.min(Math.min(deleteDistance, insertDistance),
                             matchDistance);
    }
    for (int i = 1; i < primeiraString.length(); i++) {
      int maxSourceLetterMatchIndex = primeiraString.charAt(i) == segundaString.charAt(0) ? 0
          : -1;
      for (int j = 1; j < segundaString.length(); j++) {
        Integer candidateSwapIndex = indicesDaPrimeiraStringPorCaracter.get(segundaString
            .charAt(j));
        int jSwap = maxSourceLetterMatchIndex;
        int deleteDistance = matrizDeDistancias[i - 1][j] + custoRemocao;
        int insertDistance = matrizDeDistancias[i][j - 1] + custoInsercao;
        int matchDistance = matrizDeDistancias[i - 1][j - 1];
        if (primeiraString.charAt(i) != segundaString.charAt(j)) {
          matchDistance += custoSubstituicao;
        } else {
          maxSourceLetterMatchIndex = j;
        }
        int swapDistance;
        if (candidateSwapIndex != null && jSwap != -1) {
          int iSwap = candidateSwapIndex;
          int preSwapCost;
          if (iSwap == 0 && jSwap == 0) {
            preSwapCost = 0;
          } else {
            preSwapCost = matrizDeDistancias[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
          }
          swapDistance = preSwapCost + (i - iSwap - 1) * custoRemocao
              + (j - jSwap - 1) * custoInsercao + custoTroca;
        } else {
          swapDistance = Integer.MAX_VALUE;
        }
        matrizDeDistancias[i][j] = Math.min(Math.min(Math
            .min(deleteDistance, insertDistance), matchDistance), swapDistance);
      }
      indicesDaPrimeiraStringPorCaracter.put(primeiraString.charAt(i), i);
    }
    return matrizDeDistancias[primeiraString.length() - 1][segundaString.length() - 1];
  }
}
