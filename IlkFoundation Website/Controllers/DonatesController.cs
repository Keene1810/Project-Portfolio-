using System;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using test_ngo.Models;

namespace test_ngo.Controllers
{
    public class DonatesController : Controller
    {
        private readonly string _merchantId = "70151";
        private readonly string _merchantSiteId = "88938";
        private readonly string _apiKey = "46b5fbb8a981467184df7a5680dc73bc";
        private const string _notificationUrl = "https://ILKFoundation.com/notification";
        private const string _returnUrl = "https://ILKFoundation.com/return";
        private const string _cancelUrl = "https://ILKFoundation.com/cancel";

        public IActionResult Donate()
        {
            Console.WriteLine("Donate method called.");

            return View();
        }

        public ActionResult DonatePopup()
        {
            Console.WriteLine("DonatePopup method called.");
            return PartialView("DonatePopup");
        }
        public async Task<IActionResult> DonateWithZapper(decimal amount)
        {
            Console.WriteLine($"DonateWithZapper method called with amount: {amount}");

            // Log the session creation attempt
            var sessionResponse = await CreateZapperSession(amount);

            // Log session creation success or failure
            if (sessionResponse?.RedirectUrl != null)
            {
                Console.WriteLine("Session created successfully, redirecting to Zapper.");
                return Redirect(sessionResponse.RedirectUrl);
            }

            // Log failure details for debugging
            Console.WriteLine("Session creation failed, redirecting to error page.");
            return RedirectToAction("Error", "Home");
        }

        private async Task<ZapperSessionResponse> CreateZapperSession(decimal amount)
        {
            Console.WriteLine($"CreateZapperSession method called with amount: {amount}");

            var requestBody = new
            {
                merchantOrderId = Guid.NewGuid().ToString(),
                amount = (int)(amount * 100), // Convert amount to cents for Zapper API
                currencyISOCode = "ZAR",
                notificationUrl = _notificationUrl,
                returnUrl = _returnUrl,
                cancelUrl = _cancelUrl,
                requestId = Guid.NewGuid().ToString(),
                origin = "https://ILKFoundation.com"
            };

            // Log the request body being sent to Zapper API
            Console.WriteLine($"Request Body: {JsonSerializer.Serialize(requestBody)}");

            using var client = new HttpClient();

            // Log headers before sending the request
            Console.WriteLine("Adding headers to the request:");
            Console.WriteLine($"merchantId: {_merchantId}");
            Console.WriteLine($"merchantSiteId: {_merchantSiteId}");
            Console.WriteLine($"x-api-key: {_apiKey}");

            client.DefaultRequestHeaders.Add("merchantId", _merchantId);
            client.DefaultRequestHeaders.Add("merchantSiteId", _merchantSiteId);
            client.DefaultRequestHeaders.Add("x-api-key", _apiKey);

            client.DefaultRequestHeaders.Add("User-Agent", "MyApp/1.0");
            client.DefaultRequestHeaders.Referrer = new Uri("https://ILKFoundation.com");
            client.DefaultRequestHeaders.Add("Accept", "application/json");



            // Log that the request is about to be sent
            Console.WriteLine("Sending POST request to Zapper API...");
            var response = await client.PostAsync(
                "https://gateway.zapper.com/api/v3.1/sessions",
                new StringContent(JsonSerializer.Serialize(requestBody), Encoding.UTF8, "application/json")
            );

            // Log the response status and content
            Console.WriteLine($"Response Status Code: {response.StatusCode}");
            var responseContent = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"Raw Response Content: {responseContent}");

            // Check if the response is successful
            if (response.IsSuccessStatusCode)
            {
                try
                {
                    // Log the content before attempting deserialization
                    Console.WriteLine("Attempting to deserialize the response.");

                    // Modify the model if needed to handle the response format
                    var jsonResponse = JsonSerializer.Deserialize<ZapperSessionResponse>(responseContent, new JsonSerializerOptions
                    {
                        PropertyNamingPolicy = JsonNamingPolicy.CamelCase // Ensure properties are correctly mapped
                    });

                    Console.WriteLine($"Deserialized Response: {JsonSerializer.Serialize(jsonResponse)}");

                    if (jsonResponse != null && jsonResponse.RedirectUrl != null)
                    {
                        return jsonResponse;
                    }
                    else
                    {
                        Console.WriteLine("Deserialization returned null or missing redirectUrl.");
                    }
                }
                catch (JsonException jsonEx)
                {
                    Console.WriteLine("Error deserializing response:");
                    Console.WriteLine(jsonEx.Message);
                    Console.WriteLine(responseContent);
                }
            }
            else
            {
                Console.WriteLine("Error Response from Zapper API:");
                Console.WriteLine(responseContent);
            }

            return null;
        }




        [HttpPost]
        public async Task<IActionResult> PaymentNotification([FromBody] ZapperPaymentNotification notification)
        {
            Console.WriteLine("PaymentNotification method called.");
            Console.WriteLine($"Received Notification: {JsonSerializer.Serialize(notification)}");

            if (IsSignatureValid(notification.Signature, notification))
            {
                Console.WriteLine("Signature is valid, processing notification.");
                // Process payment notification (e.g., update order status)
                return Ok();
            }

            Console.WriteLine("Invalid signature, returning BadRequest.");
            return BadRequest("Invalid signature");
        }

        private bool IsSignatureValid(string signature, ZapperPaymentNotification notification)
        {
            Console.WriteLine("IsSignatureValid method called.");
            string signatureInput = $"{_apiKey}|{notification.SessionId}|{notification.Amount}|{notification.CurrencyISOCode}|{notification.MerchantOrderId}|{notification.PaymentReference}|{notification.PaymentStatus}|{notification.PaymentUTCDate}|{notification.UserMessage ?? ""}";

            Console.WriteLine($"Signature Input: {signatureInput}");

            using (SHA256 sha = SHA256.Create())
            {
                byte[] hashBytes = sha.ComputeHash(Encoding.UTF8.GetBytes(signatureInput));
                StringBuilder builder = new StringBuilder();
                foreach (byte b in hashBytes)
                {
                    builder.Append(b.ToString("x2"));
                }
                string generatedSignature = builder.ToString();
                Console.WriteLine($"Generated Signature: {generatedSignature}");
                Console.WriteLine($"Provided Signature: {signature}");
                return generatedSignature.Equals(signature, StringComparison.OrdinalIgnoreCase);
            }
        }
    }




}
