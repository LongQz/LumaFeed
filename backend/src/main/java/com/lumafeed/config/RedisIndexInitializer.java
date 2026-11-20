package com.lumafeed.config;

import com.lumafeed.entity.Blog;
import com.lumafeed.entity.Shop;
import com.lumafeed.mapper.BlogMapper;
import com.lumafeed.mapper.ShopMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lumafeed.utils.RedisConstants.BLOG_LIKE_COUNT_KEY;
import static com.lumafeed.utils.RedisConstants.HOT_BLOG_KEY;
import static com.lumafeed.utils.RedisConstants.SHOP_GEO_KEY;

/**
 * Rebuilds Redis ranking, like counters and shop GEO indexes from MySQL.
 *
 * @author qzh
 */
@Component
public class RedisIndexInitializer implements ApplicationRunner {

    @Resource
    private BlogMapper blogMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<Blog> blogs = blogMapper.selectList(null);
        stringRedisTemplate.delete(HOT_BLOG_KEY);
        for (Blog blog : blogs) {
            int liked = blog.getLiked() == null ? 0 : blog.getLiked();
            stringRedisTemplate.opsForZSet().add(HOT_BLOG_KEY, blog.getId().toString(), liked);
            stringRedisTemplate.opsForValue().set(BLOG_LIKE_COUNT_KEY + blog.getId(), String.valueOf(liked));
        }

        Map<Long, List<Shop>> shopsByType = shopMapper.selectList(null).stream()
                .filter(shop -> shop.getX() != null && shop.getY() != null)
                .collect(Collectors.groupingBy(Shop::getTypeId));
        shopsByType.forEach((typeId, shops) -> {
            String key = SHOP_GEO_KEY + typeId;
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>();
            shops.forEach(shop -> locations.add(new RedisGeoCommands.GeoLocation<>(
                    shop.getId().toString(),
                    new Point(shop.getX(), shop.getY())
            )));
            stringRedisTemplate.opsForGeo().add(key, locations);
        });
    }
}
