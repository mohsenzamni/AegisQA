# ACS Test Scenarios — Plain Text Format
# These can be imported into AegisQA via the Scenario Explorer

## TC-001: Frictionless Visa
Add RBA adapter
Create risk chain
Set score frictionless 0..100
Do a Visa transaction
Transaction status should be Y

## TC-002: Challenge Mastercard
Add RBA adapter
Create risk chain
Set score frictionless 50..100
Do a Mastercard transaction
Transaction status should be C

## TC-003: EMV 2.3 Frictionless
Add RBA adapter
Create risk chain
Do a emv 2.3 transaction
Transaction status should be Y

## TC-004: Whitelist Flow
Add RBA adapter
Create risk chain
Check the whitelist checkbox
Do a Visa transaction
Transaction status should be Y

## TC-005: Issuer Settings + Transaction (Configurable URLs)
Configure issuer settings (BIN range, default EMV, challenge preference)
Disable purchaseDate validation in issuer setting
Do a transaction with amount/currency/cardNumber/expiry/cvv
Transaction status should be Y

## TC-006: Gmail First Mail (Generic Browser)
Navigate to https://mail.google.com
Wait for Inbox
Open first email row
Take screenshot
Assert Inbox text exists

## TC-007: Browser Search Keyword (Generic Browser)
Navigate to https://www.google.com
Fill search textbox with "AegisQA 3DS testing"
Click search button
Wait for AegisQA text
Assert AegisQA appears in results
