package repository;

import model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Контракт доступа к категориям обращений.
 */
public interface CategoryRepository {

    List<Category> findAllCategories();

    Optional<Category> findCategoryById(long id);
}
