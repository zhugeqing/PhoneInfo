/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.example.administrator.phoneinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;

public class OcrOptionActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GENERAL = 105;
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;
    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    private static final int REQUEST_CODE_ACCURATE = 108;
    private static final int REQUEST_CODE_GENERAL_ENHANCED = 109;
    private static final int REQUEST_CODE_GENERAL_WEBIMAGE = 110;
    private static final int REQUEST_CODE_BANKCARD = 111;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    private static final int REQUEST_CODE_LICENSE_PLATE = 122;
    private static final int REQUEST_CODE_BUSINESS_LICENSE = 123;
    private static final int REQUEST_CODE_RECEIPT = 124;

    private static final int REQUEST_CODE_PASSPORT = 125;
    private static final int REQUEST_CODE_NUMBERS = 126;
    private static final int REQUEST_CODE_QRCODE = 127;
    private static final int REQUEST_CODE_BUSINESSCARD = 128;
    private static final int REQUEST_CODE_HANDWRITING = 129;
    private static final int REQUEST_CODE_LOTTERY = 130;
    private static final int REQUEST_CODE_VATINVOICE = 131;
    private static final int REQUEST_CODE_CUSTOM = 132;


    private boolean hasGotToken = false;

    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_option);
        alertDialog = new AlertDialog.Builder(this);

        // 通用文字识别
        findViewById(R.id.general_basic_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
            }
        });

        // 通用文字识别(高精度版)
        findViewById(R.id.accurate_basic_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_ACCURATE_BASIC);
            }
        });

        // 通用文字识别（含位置信息版）
        findViewById(R.id.general_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL);
            }
        });

        // 通用文字识别（含位置信息高精度版）
        findViewById(R.id.accurate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_ACCURATE);
            }
        });

        // 通用文字识别（含生僻字版）
        findViewById(R.id.general_enhance_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_ENHANCED);
            }
        });

        // 网络图片识别
        findViewById(R.id.general_webimage_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_WEBIMAGE);
            }
        });

        // 身份证识别
        findViewById(R.id.idcard_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, IDCardActivity.class);
                startActivity(intent);
            }
        });

        // 银行卡识别
        findViewById(R.id.bankcard_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_BANKCARD);
            }
        });

        // 行驶证识别
        findViewById(R.id.vehicle_license_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
            }
        });

        // 驾驶证识别
        findViewById(R.id.driving_license_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE);
            }
        });

        // 车牌识别
        findViewById(R.id.license_plate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_LICENSE_PLATE);
            }
        });

        // 营业执照识别
        findViewById(R.id.business_license_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_BUSINESS_LICENSE);
            }
        });

        // 通用票据识别
        findViewById(R.id.receipt_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_RECEIPT);
            }
        });

        // 护照识别
        findViewById(R.id.passport_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_PASSPORT);
                startActivityForResult(intent, REQUEST_CODE_PASSPORT);
            }
        });

        // 二维码识别
        findViewById(R.id.qrcode_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_QRCODE);
            }
        });

        // 数字识别
        findViewById(R.id.numbers_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_NUMBERS);
            }
        });

        // 名片识别
        findViewById(R.id.business_card_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_BUSINESSCARD);
            }
        });

        // 增值税发票识别
        findViewById(R.id.vat_invoice_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VATINVOICE);
            }
        });

        // 彩票识别
        findViewById(R.id.lottery_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_LOTTERY);
            }
        });

        // 手写识别
        findViewById(R.id.handwritting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_HANDWRITING);
            }
        });

        // 自定义模板
        findViewById(R.id.custom_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(OcrOptionActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtilForOcr.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_CUSTOM);
            }
        });


        // 请选择您的初始化方式
        // initAccessToken();
        initAccessTokenWithAkSk();
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),  "So6gjZiGqj1UACzcyDFz2Rq4", "Py014u2dsmohsyvEpWO1CZVzyfMgGIsb");
    }

    /**
     * 自定义license的文件路径和文件名称，以license文件方式初始化
     */
    private void initAccessTokenLicenseFile() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("自定义文件路径licence方式获取token失败", error.getMessage());
            }
        }, "aip.license", getApplicationContext());
    }


    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final EditText et = new EditText(OcrOptionActivity.this);
                et.setText(message);
                alertDialog.setTitle(title)
                        //.setMessage(message)
                        .setView(et)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void infoPopText(final String result) {
        alertText("", result);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 识别成功回调，通用文字识别（含位置信息）
        if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recGeneral(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，通用文字识别（含位置信息高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recAccurate(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recGeneralBasic(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，通用文字识别（高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE_BASIC && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recAccurateBasic(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，通用文字识别（含生僻字版）
        if (requestCode == REQUEST_CODE_GENERAL_ENHANCED && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recGeneralEnhanced(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，网络图片文字识别
        if (requestCode == REQUEST_CODE_GENERAL_WEBIMAGE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recWebimage(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，银行卡识别
        if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recBankCard(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，行驶证识别
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recVehicleLicense(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，驾驶证识别
        if (requestCode == REQUEST_CODE_DRIVING_LICENSE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recDrivingLicense(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，车牌识别
        if (requestCode == REQUEST_CODE_LICENSE_PLATE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recLicensePlate(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，营业执照识别
        if (requestCode == REQUEST_CODE_BUSINESS_LICENSE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recBusinessLicense(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                new OcrRecognizeService.ServiceListener() {
                    @Override
                    public void onResult(String result) {
                        infoPopText(result);
                    }
                });
        }

        // 识别成功回调，通用票据识别
        if (requestCode == REQUEST_CODE_RECEIPT && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recReceipt(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                new OcrRecognizeService.ServiceListener() {
                    @Override
                    public void onResult(String result) {
                        infoPopText(result);
                    }
                });
        }

        // 识别成功回调，护照
        if (requestCode == REQUEST_CODE_PASSPORT && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recPassport(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                new OcrRecognizeService.ServiceListener() {
                    @Override
                    public void onResult(String result) {
                        infoPopText(result);
                    }
                });
        }

        // 识别成功回调，二维码
        if (requestCode == REQUEST_CODE_QRCODE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recQrcode(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，彩票
        if (requestCode == REQUEST_CODE_LOTTERY && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recLottery(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，增值税发票
        if (requestCode == REQUEST_CODE_VATINVOICE && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recVatInvoice(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，数字
        if (requestCode == REQUEST_CODE_NUMBERS && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recNumbers(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，手写
        if (requestCode == REQUEST_CODE_HANDWRITING && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recHandwriting(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，名片
        if (requestCode == REQUEST_CODE_BUSINESSCARD && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recBusinessCard(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        // 识别成功回调，自定义模板
        if (requestCode == REQUEST_CODE_CUSTOM && resultCode == Activity.RESULT_OK) {
            OcrRecognizeService.recCustom(this, FileUtilForOcr.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new OcrRecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance(this).release();
    }
}
