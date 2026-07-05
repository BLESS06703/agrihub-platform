# AgriHub Platform - Error Code Catalog

## Authentication Errors
| Code | HTTP | EN Message | NY Message |
|------|------|------------|------------|
| INVALID_CREDENTIALS | 401 | Incorrect email or password. Please try again. | Imelo kapena password yolakwika. Yesaninso. |
| TOKEN_EXPIRED | 401 | Your session has expired. Please sign in again. | Nthawi yanu yatha. Lowani momwe mungayambire. |
| ACCOUNT_LOCKED | 423 | Account locked after multiple failed attempts. Try again in 30 minutes. | Akaunti yatsekedwa. Yesaninso pakatha mphindi 30. |

## Permission Errors
| FORBIDDEN | 403 | You do not have permission to access this resource. | Mulibe chilolezo chowonera izi. |
| TENANT_MISMATCH | 403 | Requested resource belongs to another organization. | Zomwe mukufuna ndi za bungwe lina. |

## Validation Errors
| VALIDATION_ERROR | 422 | Please check your input and try again. | Chonde onaninso zomwe mwalemba. |
| DUPLICATE_ENTRY | 409 | This record already exists. | Izi zilipo kale. |
| NOT_FOUND | 404 | The requested resource was not found. | Zomwe mukufuna sizikupezeka. |

## System Errors
| INTERNAL_ERROR | 500 | An unexpected error occurred. Our team has been notified. | Vuto ladzidzidzi lachitika. Tikukonza. |
| SERVICE_UNAVAILABLE | 503 | Service temporarily unavailable. Please try again later. | Ntchito ikupezeka pano. Yesaninso nthawi ina. |
| RATE_LIMITED | 429 | Too many requests. Please wait and try again. | Mwatumiza zopempha zambiri. Dikirani kaye. |
