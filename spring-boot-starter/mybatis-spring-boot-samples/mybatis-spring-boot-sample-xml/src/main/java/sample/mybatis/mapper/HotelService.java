package sample.mybatis.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dongyinuo
 * @date 2021-06-23
 */
@Service
public class HotelService {

    @Autowired
    private HotelMapper hotelMapper;

    @Transactional
    public void testCache(){
        System.out.println(hotelMapper.selectByCityId(1, "a"));
        System.out.println(hotelMapper.selectByCityId(1, "a"));
        System.out.println(hotelMapper.selectByCityId(1, "a"));
    }
}
