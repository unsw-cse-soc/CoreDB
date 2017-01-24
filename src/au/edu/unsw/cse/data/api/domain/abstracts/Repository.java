package au.edu.unsw.cse.data.api.domain.abstracts;

import java.util.List;

public interface Repository<T> {

  T get(String id);

  List<T> getAll();

  void create(T entity);

  void delete(String id);
}
