<?php
require __DIR__ . '/Api/vendor/autoload.php';


use Dotenv\Dotenv;
use Slim\Factory\AppFactory;
use App\Controller\CalculationController;
use App\Controller\LawWebsitiesController;
use App\Controller\CategoryController;
use App\Middleware\DeviceIDMiddleware;
use App\Middleware\RateLimitMiddleware;
use App\Middleware\ApiKeyMiddleware;

$env = Dotenv::createImmutable( __DIR__ . '/Api');

$env->load();

$app = AppFactory::create();

//========================
//Global middleware
//========================

if(isset($_ENV["DEBUG"]) && $_ENV["DEBUG"] === "false"){

    $error = $app->addErrorMiddleware(true, true, true);
    $app->add(new DeviceIDMiddleware());
    $app->add(new ApiKeyMiddleware());
    $app->add(new RateLimitMiddleware());

}

//========================
//Route
//========================
//GET Request

$app->get('/limt_item', CalculationController::class .':limt_item');

$app->get('/all_item', CalculationController::class .':all_item');

$app->get('/websites', LawWebsitiesController::class .':websites');

$app->get('/all_category', CategoryController::class . ':all_category');


//POST Request

$app->post('/category', CategoryController::class .':category');



$app->run();