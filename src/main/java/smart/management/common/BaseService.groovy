package smart.management.common

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@Service
@ControllerAdvice
abstract class BaseService<T, ID> {

    class ResourceNotFoundException extends Exception {
        ResourceNotFoundException(String message) { super(message) }
    }

    abstract PagingAndSortingRepository<T, ID> getRepository()

    T findById(ID id) {
        Optional<T> optional = getRepository().findById(id)
        if (optional != null && optional.isPresent()) {
            return optional.get()
        } else {
            throw new ResourceNotFoundException("Resource cannot be found!")
        }
    }

    T save(T t) {
        if (t instanceof BaseDocument) {
            t.lastModifiedDate = new Date()
        }
        return getRepository().save(t)
    }

    void deleteById(ID id) {
        getRepository().deleteById(id)
    }

    Iterable<T> findAll() {
        return getRepository().findAll()
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    ServerResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: exception.toString())
    }

}
