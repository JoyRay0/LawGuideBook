<?php
require __DIR__ . '/Api/vendor/autoload.php';


use App\Controller\InitController;
use App\Middleware\HeaderMiddleware;
use Dotenv\Dotenv;
use Slim\Factory\AppFactory;
use App\Controller\CalculationController;
use App\Controller\LawWebsitiesController;
use App\Controller\CategoryController;
use App\Middleware\DeviceIDMiddleware;
use App\Middleware\RateLimitMiddleware;
use App\Middleware\ApiKeyMiddleware;
use App\Controller\AdController;
use App\Controller\SearchController;
use App\Controller\AnswerController;
use App\Controller\AppFeatureController;
use App\Controller\ChatbotController;
use App\Controller\SearchSuggestion;

$env = Dotenv::createImmutable( __DIR__ . '/Api');

$env->load();

$app = AppFactory::create();

//========================
//Global middleware
//========================

if(isset($_ENV["DEBUG"]) && $_ENV["DEBUG"] === "false"){

    $app->add(new DeviceIDMiddleware());
    $app->add(new ApiKeyMiddleware());
    $app->add(new RateLimitMiddleware());

}else{

    $error = $app->addErrorMiddleware(true, true, true);

}

$app->addBodyParsingMiddleware();
$app->add(new HeaderMiddleware());

//========================
//Route
//========================
//GET Request

$app->get('/calculation_limit', CalculationController::class .':calculation_limt_item');

$app->get('/calculation_all', CalculationController::class .':calculation_all_item');

$app->get('/websites', LawWebsitiesController::class .':websites');

$app->get('/all_category', CategoryController::class .':all_category');

$app->get('/ads', AdController::class .':ads');

$app->get('/app_update', AppFeatureController::class .':app_update');


//POST Request

$app->post('/category/{page}', CategoryController::class .':category');

$app->post('/search/{page}', SearchController::class .':search');

$app->post('/answer', AnswerController::class .':answer');

$app->post('/ai_chat', ChatbotController::class .':chatbot');

$app->post('/suggestion', SearchSuggestion::class . ':suggestion');



$app->run();