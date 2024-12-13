package tasks;

import common.Person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлены чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй
  public List<String> getNames(List<Person> persons) {
    // Сама IDEA подсказывает заменить == 0 на .isEmpty()
    if (persons.isEmpty()) {
      return Collections.emptyList();
    }
    // Операция удаления первого элемента какая-то отдельная, включим ее в пайплайн в стриме
    // + не мутируем список persons
    return persons.stream()
        .skip(1)
        .map(Person::firstName)
        .collect(Collectors.toList());
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  public Set<String> getDifferentNames(List<Person> persons) {
    // Здесь можно обойтись без Stream API и просто в конструктор передать коллекцию
    return new HashSet<>(getNames(persons));
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  public String convertPersonToString(Person person) {
    // Для конкатенации лучше использовать StringBuilder, чтобы не загромождать String pool
    StringBuilder result = new StringBuilder();
    // Сократим код для обработки null-кейсов с использованием Optional
    Optional.ofNullable(person.secondName()).ifPresent(result::append);
    Optional.ofNullable(person.firstName()).ifPresent(firstName -> result.append(" ").append(firstName));
    // По логике должно быть middleName - отчество, а не secondName - фамилия (в исходном коде было secondName)
    Optional.ofNullable(person.middleName()).ifPresent(middleName -> result.append(" ").append(middleName));
    return result.toString();
  }

  // словарь id персоны -> ее имя
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    // Переписали на Stream API для наглядности
    return persons.stream()
      .collect(Collectors.toMap(
          Person::id,
          this::convertPersonToString,
          (existing, replacement) -> existing
        )
      );
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    // Пусть n и m - размер коллекций 1 и 2
    // Проблема старого кода во вложенности - асимптотика поиска всегда O(nm)
    // Асимптотика нового кода зависит от сложности операции contains, но даже в худшем случае будет O(nm)
    return !persons2.stream()
        .filter(persons1::contains)
        .toList()
        .isEmpty();
  }

  // Посчитать число четных чисел
  public long countEven(Stream<Integer> numbers) {
    // Сократили ненужный код
    return numbers.filter(num -> num % 2 == 0).count();
  }

  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке
  void listVsSet() {
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    assert snapshot.toString().equals(set.toString());
  }
  // Насколько я понял, это связано с тем, что при вызове toString() для HashSet
  // элементы будут возвращены в порядке, определенном хэш-кодами элементов
  // Но хэш-коды Integer - это само значение числа, поэтому и принтуется в отсортированном порядке
}
