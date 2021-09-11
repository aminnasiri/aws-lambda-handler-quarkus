# AWS Lambda with Java, a Fast and Low-Cost Approach

- [Abstract](##Abstract)
- [The demo application](##The-demo-application)
  - [The demo application workflow](###The-demo-application-workflow)
  - [Creating a Quarkus based Java application](###Creating-a-Quarkus-based-Java-application)
  - [The benefits of using Quarkus to develop a Java Application on AWS Lambda](###The-benefits-of-using-Quarkus-to-develop-a-Java-Application-on-AWS-Lambda)
  - [Deploying the demo application on AWS Lambda](###Deploying-the-demo-application-on-AWS-Lambda)
  - [Watching the performance of the demo application on AWS Lambda + JVM platform](###Watching-the-performance-of-the-demo-application-on-AWS-Lambda-+-JVM-platform)
- [The available solutions for the AWS Lambda cold-start challenge](##The-available-solutions-for-the-AWS-Lambda-cold-start-challenge)
- [What is GraalVM?](##What-is-GraalVM?)
- [Building a native binary executable from the Java application](##Building-a-native-binary-executable-from-the-Java-application)
  - [AWS Lambda Environment](###AWS-Lambda-Environment)
  - [Deploying the binary executable on AWS Lambda](###Deploying-the-binary-executable-on-AWS-Lambda)
  - [Watching the performance of the demo application on AWS Lambda + Custom platform](###Watching-the-performance-of-the-demo-application-on-AWS-Lambda-+-Custom-platform)
- [Analyzing the performance of JVM vs Native Binary on AWS Lambda](##Analyzing-the-performance-of-JVM-vs-Native-Binary-on-AWS-Lambda)
- [Conclusion](##Conclusion)
- [References](##References)

## Abstract

The AWS Lambda is a popular platform for serverless development, and as a Java developer,
I like to be able to use this platform, but there are some essential points that need to be addressed first.

1) The cost of serverless functions on AWS Lambda would be expensive with the JVM.
2) The Cold start on AWS Lambda can be a real issue on the JVM.
3) The maximized efficiency on AWS Lambda for each request can be costly, and it can not be very efficient with the JVM.

The two primary purposes of this article are as follows:
- Learning how to use AWS service, e.g. DynamoDB by Quarkus framework on the serverless platform(Lambda).
- Having the best performance on AWS Lambda and make a minimum cost.

## The demo application

This repository contains an example of a Java application developed by JDK 11 & [Quarkus](https://quarkus.io), which is a
simple AWS Lambda function. This simple function will accept a fruit name in a JSON format(input) and return a type
of fruit.

```json
{
  "name": "Apple"
}
```

The type of fruit will be.

+ **Spring** Season Fruit
+ **Summer** Season Fruit
+ **Fall** Season Fruit
+ **Winter** Season Fruit

![](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/images/handler-diagram.png)



### The demo application workflow

This demo is a simple Java application that fetches the requested Fruit information, extracts the type of fruit, and returns the correct type of fruit. How simple is that?!


![](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/images/sequence-diagram.png)


### Creating a Quarkus based Java application

Quarkus offers a clear [guideline](https://quarkus.io/guides/amazon-lambda) which expands on the AWS Lambda project.
This project template can be easily accessed by using a Maven command.

```shell
mvn archetype:generate \
       -DarchetypeGroupId=com.thinksky \
       -DarchetypeArtifactId=aws-lambda-handler-qaurkus \
       -DarchetypeVersion=2.1.3.Final 
```
The command will generate an application using AWS Java SDK.

The Quarkus framework has extensions for DynamoDB, S3, SNS, SQS, etc., and I prefer using AWS Java SDK V2 which offers non-blocking features.
So, the project pom.xml file needs to be modified by [this guideline](https://quarkus.io/guides/amazon-lambda#aws-sdk-v2).

The project has Lambda, a dependency inside of the pom file.

```xml

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-amazon-lambda</artifactId>
</dependency>
```

Adding a dependency to use AWS DynamoDB to make a connection to DynamoDB is required

```xml

<dependencies>

    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-amazon-dynamodb</artifactId>
    </dependency>

    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-apache-httpclient</artifactId>
    </dependency>

    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>apache-client</artifactId>
        <exclusions>
            <exclusion>
                <artifactId>commons-logging</artifactId>
                <groupId>commons-logging</groupId>
            </exclusion>
        </exclusions>
    </dependency>

</dependencies>
```

I will use apache client on the setting of the application which can be added using the `apache-client` dependency.

```properties
quarkus.dynamodb.sync-client.type=apache
```

### The benefits of using Quarkus to develop a Java Application on AWS Lambda

A regular AWS Lambda Java project will be a plain Java project; however, Quarkus will bring Dependency-Injection inside a Java project.

```java

@ApplicationScoped
public class FruitService extends AbstractService {

    @Inject
    DynamoDbClient dynamoDB;

    public List<Fruit> findAll() {
        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(Fruit::from)
                .collect(Collectors.toList());
    }

    public List<Fruit> add(Fruit fruit) {
        dynamoDB.putItem(putRequest(fruit));
        return findAll();
    }

}
```

`DynamoDbClient` is a class from AWS Java SDK.v2 which Quarkus will build and make available in its Dependency Injection ecosystem.
The [FruitService](https://github.com/aminnasiri/aws-lambda-handler-quarkus/tree/main/src/main/java/com/thinksy/service/FruitService.java) is inherited from an abstract class called [AbstractService](https://github.com/aminnasiri/aws-lambda-handler-quarkus/tree/main/src/main/java/com/thinksy/service/AbstractService.java),
and this abstract class will provide basic objects of `DynamoDbClient` needs, e.g. `ScanRequest`, `PutItemRequest`, etc.

Reflection is popular in Java frameworks, but it will be a new challenge on GraalVM native-image. (more information [reflection in Graalvm](https://www.graalvm.org/reference-manual/native-image/Reflection/))
But Quarkus has a simple solution for this challenge, and that's an annotation on classes `@RegisterForReflection`. Isn't it the easiest way to register a class for reflection in GraalVM?


```java
@RegisterForReflection
public class Fruit {

    private String name;
    private Season type;

    public Fruit() {
    }

    public Fruit(String name, Season type) {
        this.name = name;
        this.type = type;
    }
}
``` 

It is also worth mentioning that Quarkus offers many other benefits while using the AWS Lambda platform. I will describe them in a series of future articles.

### Deploying the demo application on AWS Lambda

It is deployment time on AWS, and the process will be relatively simple using Maven and Quarkus framework.
However, more preparation on AWS is required prior to deployment and running the application.
The deployment process consists of the following steps:


**1)** Define table of Fruits_TBL in DynamoDB

```shell
$ aws dynamodb create-table --table-name Fruits_TBL \
                          --attribute-definitions AttributeName=fruitName,AttributeType=S \
                          AttributeName=fruitType,AttributeType=S \
                          --key-schema AttributeName=fruitName,KeyType=HASH \
                          AttributeName=fruitType,KeyType=RANGE \
                          --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

Then insert some items of fruits on the table.

```shell
$ aws dynamodb put-item --table-name Fruits_TBL \
        --item file://item.json \
        --return-consumed-capacity TOTAL \
        --return-item-collection-metrics SIZE
```

Here is the content of item.json

```json
{
  "fruitName": {
    "S": "Apple"
  },
  "fruitType": {
    "S": "Fall"
  }
}
```

Finally, run the query from Dynamodb to make sure we have items.

```shell
$ aws dynamodb query \
     --table-name  Fruits_TBL \ 
     --key-condition-expression "fruitName = :name" \
     --expression-attribute-values '{":name":{"S":"Apple"}}'
```

**2)** Define a role in IAM to have access to DynamoBD and assign it to our Lambda application.

```shell
$ aws iam create-role --role-name fruits_service_role --assume-role-policy-document file://policy.json
```

Here is policy.json

```json
{
  "Version": "2012-10-17",
  "Statement": {
    "Effect": "Allow",
    "Principal": {
      "Service": [
        "dynamodb.amazonaws.com",
        "lambda.amazonaws.com"
      ]
    },
    "Action": "sts:AssumeRole"
  }
}
```

Then, assign the DynamoDB permission to this role

```shell
$ aws iam attach-role-policy --role-name fruits_service_role --policy-arn "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"
```
Then

```shell
$ aws iam attach-role-policy --role-name fruits_service_role --policy-arn "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
```

And the role might need the following permission as well

```shell
$ aws iam attach-role-policy --role-name fruits_service_role --policy-arn "arn:aws:iam::aws:policy/AWSLambda_FullAccess"
```

Finally, The AWS platform is ready to host our application now.

**To continue with the deployment process**, we need to build our application and modify the generated articles.

```shell
$  mvn clean install
```

The Quarkus framework will take care of creating a JAR artifact file, zip this JAR file, and prepare the [SAM template](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-specification.html) of AWS.
The JVM version should be used this time, and here is how it can be modified:

**1)** Add a defined role to Lambda to have proper access

```yaml
  Role: arn:aws:iam::{Your-Account-Number-On-AWS}:role/fruits_service_role
```

**2)** Increase the timeout

```yaml
  Timeout: 120
```

So SAM template is ready to deploy on AWS Lambda now.

```shell
$  sam deploy -t target/sam.jvm.yaml -g
```

This command will upload the jar file as a zip format to AWS and deploy it as Lambda Function.
The next step will be to test the application by invoking a request

### Watching the performance of the demo application on AWS Lambda + JVM platform

It's time to run the deployed Lambda function, test it, and see how well it performs.

````shell
$ aws lambda invoke response.txt --cli-binary-format raw-in-base64-out --function-name {"FUNCTION_NAME":fruitApp} --payload file://payload.json --log-type Tail --query LogResult --output text | base64 --decode
````

We can figure out the **FUNCTION_NAME** by using the following command.

```shell
$ aws lambda list-functions --query 'Functions[?starts_with(FunctionName, `fruitAppJVM`) == `true`].FunctionName'
```

_fruitAppJVM_ is the name of Lambda I gave to SAM CLI in the deployment process.

Then we can refer to the AWS web console to see the results of invoking the function.

![](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/images/fruit-app-jvm-performace.png)

Numbers are talking, and this is a horrible performance for a simple application due to AWS Lambda’s cold-start feature.

#### What is an AWS Lambda cold start?

When running a Lambda function, it stays active as long as it’s being actively used, which means that your container stays alive and ready for execution.
However, AWS will drop the container after a period of inactivity (usually very short), and your function becomes inactive or cold.
A cold start occurs when a request comes to the idle lambda function. Afterwards, the Lambda function will be initialized to be able to respond to the request. (initialize mode of Java framework).

On the other hand, a warm start happens when there are available lambda containers. For more information, follow [this link](https://aws.amazon.com/blogs/compute/operating-lambda-performance-optimization-part-1/)


The cold start is the main reason we have this horrible performance as each time the cold start occurs, AWS will initialize our Java application, and obviously, it will take a long time for each request.

## The available solutions for the AWS Lambda cold-start challenge

There are two approaches to this fundamental challenge.

+ Using Provisioned Concurrency which is not the scope of this article, please
  visit [Predictable start-up times with Provisioned Concurrency](https://aws.amazon.com/blogs/compute/new-for-aws-lambda-predictable-start-up-times-with-provisioned-concurrency/)
+ Having better performance on initialize and response times of the application which brings up the question of how we can achieve better performance in our Java application. The answer is to create a native binary executable from our Java application and deploy it on AWS Lambda with [Oracle GraalVM](https://graalvm.org).

## What is GraalVM?

GraalVM is a high-performance JDK distribution designed to accelerate the execution of applications written in **Java**
and other JVM languages along with support for **JavaScript**, **Ruby**, **Python**, and a number of other popular
languages. Native-Image is an ahead-of-time technology which compiles Java code to a standalone executable.
This executable includes the application classes, classes from its dependencies, runtime library classes, and
statically linked native code from JDK. It does not run on the Java VM, but includes necessary components like memory
management, thread scheduling, and so on from a different runtime system, called “Substrate VM”.

## Building a native binary executable from the Java application

First, we need to install GraalVM and its Native-Image using [this guideline](https://thinksky.com/blog/2021/07/20/install-graalvm-native-image/).
Then, by installing GraalVM, we can convert a Java application to a native binary executable with GraalVM.
Quarkus makes it easy, and it has a Maven/Gradle plugin, so in a typical Quarkus based application, we will have a profile called `native`.


```shell
$  mvn clean install -Pnative
```

Maven will build a native binary executable file based on the OS you are using.
If you are developing on Windows, this file will be only able to run on Windows machines; however, AWS Lambda requires Linux based binary executable.
In this case, the Quarkus framework will cover this requirement by a simple parameter on its plugin `-Dquarkus.native.container-build=true`.

```shell
$  mvn clean install -Pnative \
        -Dquarkus.native.container-build=true
```

### AWS Lambda Environment

AWS Lambda has a couple of different deployable environments.

|Runtime   |Amazon Linux   |Amazon Linux 2 (AL2)  |
|----------|:--------------|:---------------------|
|Node.js   |nodejs12.x     |nodejs10.x            |
|Python   |python3.7, python3.6 |python3.8        |
|Ruby     |ruby2.5         |ruby2.7               |
|Java     |java            |java11 (Corretto 11), java8.al2 (Corretto 8)   |
|Go       |go1.x           |    provided.al2      |
|.NET     |dotnetcore2.1   |dotnetcore3.1         |
|Custom   |provided       |provided.al2           |

So we previously deployed the Java Application on Lambda by java11 (Corretto 11), and it didn't show a good performance.

We currently have two options for the pure Linux platform on Lambda, which are `provided` and `provided.al2`.

It is worth mentioning that `provided` will use Amazon Linux, and `provided.al2` will use [Amazon Linux 2](https://aws.amazon.com/amazon-linux-2/faqs/),
so, because of long-term support of version 2, using Version 2 is highly recommended.

### Deploying the binary executable on AWS Lambda

As we saw, Quarkus will produce two sam templates for us; one is for the JVM base Lambda and the second one is the native binary executable. We should use the native sam template this time which also needs some slight modifications.

1) Change to AWS Linux V2

```yaml
  Runtime: provided.al2
```

2) Add the defined role to Lambda to have proper access.

```yaml
  Role: arn:aws:iam::{Your-Account-Number-On-AWS}:role/fruits_service_role
```

3) Increase the timeout

```yaml
  Timeout: 30
```

The final version of the native SAM template will be like this [final.sam.native.yaml](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/final.sam.native.yaml); it is now ready to be deployed on AWS.

```
$ sam deploy -t target/sam.native.yaml -g
```

This command will upload the binary file as a zip format to AWS and deploy it as Lambda Function, exactly like the JVM version.
Now, we can jump to the exciting part, which is monitoring performance.

### Watching the performance of the demo application on AWS Lambda + Custom platform

It's time to run the deployed Lambda function, test it, and see how well it performs.

````shell
$ aws lambda invoke response.txt --cli-binary-format raw-in-base64-out --function-name {"FUNCTION_NAME":fruitApp} --payload file://payload.json --log-type Tail --query LogResult --output text | base64 --decode
````

We can figure out the **FUNCTION_NAME** by using the below command.

```shell
$ aws lambda list-functions --query 'Functions[?starts_with(FunctionName, `fruitAppNative`) == `true`].FunctionName'
```

_fruitAppNative_ is the name of Lambda I gave to SAM CLI in the deployment process.

Then we can open the AWS web console to see the results of invoking the function.
![](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/images/fruit-app-native-binary-performace.png)

Wow, such a fantastic result is showing up.

## Analyzing the performance of JVM vs Native Binary on AWS Lambda

We can analyze and compare both versions of the application on the AWS Lambda platform in two categories.

+ **Initialize time:**
  The time consumed by the first call or invoke the Lambda function is called **initialize time**.
  It is almost the longest duration of invoking an application on Lambda because our Java application will start from scratch in this phase.

  ![](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/images/initialize-time.png)

+ There is a considerable difference between JVM and the Binary version, which means the initialized time of the native binary version is almost **_eight times_** faster than the JVM version.


+ **Request time:**
  I invoked the Lambda function nine times after initialized step, and here is the performance result.

  ![](https://github.com/aminnasiri/aws-lambda-handler-quarkus/blob/main/images/invoke-times.png)

  Based on the result, there is a significant difference in the performance between the JVM version and the Native binary.



## Conclusion

Quarkus framework will help us have clear and structured code on Java application by providing some good features like Dependency-Injection. Also, it will help to convert our Java application to a native-binary file with the help of GraalVM.

The native binary version has a significantly better performance compared to the JVM version.

- The binary version uses just 128 MB of ram, whereas the JVM version uses 512 MB, which results in saving a considerable amount of resources on AWS Lambda.
- The binary version offers better request times than the JVM version, which means more time-saving on AWS Lambda.

Overall, by saving resources and time, the native binary approach has proven to be a low-cost option.


## References

+ [GraalVM](https://www.graalvm.org)
+ [GraalVM NativeImage](https://www.graalvm.org/reference-manual/native-image/)
+ [GraalVM Native Image Support in the AWS SDK for Java 2.x](https://aws.amazon.com/blogs/developer/graalvm-native-image-support-in-the-aws-sdk-for-java-2-x/)
+ [Quarkus](https://quarkus.io)
+ [QUARKUS - AMAZON LAMBDA](https://quarkus.io/guides/amazon-lambda)
+ [Optimize your Java application for AWS Lambda with Quarkus](https://aws.amazon.com/blogs/architecture/field-notes-optimize-your-java-application-for-aws-lambda-with-quarkus/)
+ [An article of running Java app on Lambda](https://bmccann.medium.com/is-quarkus-the-magic-bullet-for-java-and-aws-lambda-567a0968a971)