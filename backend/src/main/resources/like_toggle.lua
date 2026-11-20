local likedKey = KEYS[1]
local countKey = KEYS[2]
local hotKey = KEYS[3]
local userId = ARGV[1]
local blogId = ARGV[2]

if redis.call('SISMEMBER', likedKey, userId) == 1 then
    redis.call('SREM', likedKey, userId)
    local count = tonumber(redis.call('GET', countKey) or '0')
    if count > 0 then
        redis.call('DECR', countKey)
    end
    redis.call('ZINCRBY', hotKey, -1, blogId)
    return -1
end

redis.call('SADD', likedKey, userId)
redis.call('INCR', countKey)
redis.call('ZINCRBY', hotKey, 1, blogId)
return 1
