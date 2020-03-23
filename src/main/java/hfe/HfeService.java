package hfe;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HfeService {

    private final UserMapper userMapper;

    public HfeService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
