package tasks;

import common.Area;
import common.Person;

import java.util.*;
import java.util.stream.Collectors;

/*
Имеются
- коллекция персон Collection<Person>
- словарь Map<Integer, Set<Integer>>, сопоставляющий каждой персоне множество id регионов
- коллекция всех регионов Collection<Area>
На выходе хочется получить множество строк вида "Имя - регион". Если у персон регионов несколько, таких строк так же будет несколько
 */
public class Task6 {

  public static Set<String> getPersonDescriptions(Collection<Person> persons,
                                                  Map<Integer, Set<Integer>> personAreaIds,
                                                  Collection<Area> areas) {
    // Доп. память O(m), где m - кол-во регионов
    Map<Integer, Area> areaMap = areas.stream()
        .collect(Collectors.toMap(Area::getId, area -> area, (existing, replacement) -> existing));

    // Теперь сложность кода O(n + m), т.к. мы не бегаем для каждого person каждый раз в areas
    // Вместо этого можем за О(1) искать в areaMap название региона по его id
    return persons.stream()
      .flatMap(person -> personAreaIds.getOrDefault(person.id(), Collections.emptySet()).stream()
            .map(areaMap::get)
            .filter(Objects::nonNull)
            .map(area -> formatOutput(person, area)))
      .collect(Collectors.toSet());
  }

  private static String formatOutput(Person person, Area area) {
    return String.format("%s - %s", person.firstName(), area.getName());
  }
}
