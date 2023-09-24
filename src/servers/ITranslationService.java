package servers;

public interface ITranslationService {
  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the word to be translated
   * @param value the translation
   * @return the outcome of the operation
   */
  String put(String key, String value);

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   */
  String get(String key);

  /**
   * Removes a key-value pair.
   *
   * @param key the word to be deleted
   * @return the outcome of the operation
   */
  String delete(String key);
}
