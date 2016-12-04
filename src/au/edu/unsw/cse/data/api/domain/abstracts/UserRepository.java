package au.edu.unsw.cse.data.api.domain.abstracts;

import au.edu.unsw.cse.data.api.domain.entity.User;

public interface UserRepository extends Repository<User> {
	User get(String userName, String password);

	User getByUserNameClientId(String userName, String clientId);
}
