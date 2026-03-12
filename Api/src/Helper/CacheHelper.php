<?php

namespace App\Helper;

use Phpfastcache\Helper\Psr16Adapter;
use Phpfastcache\Config\ConfigurationOptionInterface;
use Phpfastcache\Drivers\Memcached\Config as MemcachedConfig;
class CacheHelper{

    private static $cache;

    
    private static function init(){

        if(!self::$cache){

            $config = new MemcachedConfig();
            $config->setHost("localhost");
            $config->setPort(11211);

            self::$cache = new Psr16Adapter('Memcached', $config);

        }

    }

    public static function setStringCache(string $key, string $value, int $endTime){

        self::init();
        self::$cache->set($key, $value, $endTime);

    }

    public static function setArrayCache(string $key, array $value, int $endTime){

        self::init();
        self::$cache->set($key, $value, $endTime);

    }

    public static function getStringCache(string $key) : string{


        self::init();
        $data = self::$cache->get($key) ?: "";

        return $data;
    }

    public static function getArrayCache(string $key) : array{


        self::init();
        $data = self::$cache->get($key) ?: [];

        return $data;
    }

    public static function deleteCache(string $key) : bool{

        self::init();
        $data = self::$cache->delete($key) ?: false;

        return $data;

    }

    public static function deleteAllCache():bool{

        self::init();
        $data = self::$cache->clear() ?: false;

        return $data;
    }


}