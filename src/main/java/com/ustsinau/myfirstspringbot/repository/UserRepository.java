package com.ustsinau.myfirstspringbot.repository;

import com.ustsinau.myfirstspringbot.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
