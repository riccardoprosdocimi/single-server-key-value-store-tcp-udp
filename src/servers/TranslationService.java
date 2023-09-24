package servers;

import java.util.HashMap;


/**
 * An implementation of the Translation Service Interface.
 */
public class TranslationService implements ITranslationService {
  private final HashMap<String, String> dictionary = new HashMap<>();

  /**
   * Saves a key-value pair in a hashmap.
   *
   * @param key   the word to be translated
   * @param value the translation
   * @return the outcome of the operation
   */
  @Override
  public String put(String key, String value) {
    this.dictionary.put(key.toLowerCase(), value.toLowerCase());
    return "SUCCESS";
  }

  /**
   * Retrieves the value of a key.
   *
   * @param key the word to be translated
   * @return the translation
   */
  @Override
  public String get(String key) {
    key = key.toLowerCase();
    String translation;
    if (this.dictionary.containsKey(key)) {
      translation = this.dictionary.get(key);
    } else {
      translation = "FAIL: I don't know the translation for " + "\"" + key + "\"" + " yet";
    }
    return translation;
  }

  /**
   * Removes a key-value pair.
   *
   * @param key the word to be deleted
   * @return the outcome of the operation
   */
  @Override
  public String delete(String key) {
    key = key.toLowerCase();
    if (this.dictionary.containsKey(key)) {
      this.dictionary.remove(key);
      return "SUCCESS";
    } else {
      return "FAIL: " + "\"" + key + "\" " + "does not exist";
    }
  }
}
