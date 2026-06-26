package gytis.courier.adapter.in.rest.person;

import gytis.courier.adapter.in.pagination.PageQueryAssembler;
import gytis.courier.adapter.in.rest.person.dto.AdminPersonSearchRequest;
import gytis.courier.adapter.in.rest.person.dto.PersonDetailsUpdateRequest;
import gytis.courier.adapter.in.rest.person.pagination.PersonAdminPagingPolicy;
import gytis.courier.application.common.PageQuery;
import gytis.courier.application.common.PageResult;
import gytis.courier.application.port.in.person.PersonQueryUseCase;
import gytis.courier.application.port.in.person.PersonCommandUseCase;
import gytis.courier.application.query.filter.PersonQuery;
import gytis.courier.application.readmodel.person.AdminPersonDetailsReadModel;
import gytis.courier.application.readmodel.person.AdminPersonListReadModel;
import gytis.courier.adapter.in.rest.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonCommandUseCase personCommandUseCase;
    private final PersonQueryUseCase queryUseCase;
    private final PersonRequestMapper requestMapper;

    public PersonController(PersonCommandUseCase personCommandUseCase, PersonQueryUseCase queryUseCase, PersonRequestMapper requestMapper) {
        this.personCommandUseCase = personCommandUseCase;
        this.queryUseCase = queryUseCase;
        this.requestMapper = requestMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{personId}")
    public AdminPersonDetailsReadModel fetchById(@PathVariable Long personId) {
        return queryUseCase.getAdminDetailedById(personId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PageResult<AdminPersonListReadModel> fetchAllPersons(AdminPersonSearchRequest r) {
        PersonQuery query = new PersonQuery(r.role(), r.searchKey());
        PageQuery pageQuery = PageQueryAssembler.from(r.page(), r.size(), r.sortField(), r.direction(), PersonAdminPagingPolicy.INSTANCE);
        return queryUseCase.getAll(query, pageQuery);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updatePerson(@PathVariable Long id,
                                               @RequestBody PersonDetailsUpdateRequest updateRequest) {
        personCommandUseCase.updatePersonDetails(requestMapper.toUpdateCommand(updateRequest), id);
        return ResponseEntity.ok(new ApiResponse("success", "Person was successfully updated."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        personCommandUseCase.deletePerson(id);
        return ResponseEntity.ok(new ApiResponse("success", "Person was successfully deleted."));
    }
}
