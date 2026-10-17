package repository;

import model.Department;

import java.util.List;
import java.util.Optional;

/**
 * Контракт доступа к подразделениям.
 */
public interface DepartmentRepository {

    List<Department> findAllDepartments();

    Optional<Department> findDepartmentById(long id);
}
