//
//  KKModelBaseElement.m
//  KKOBD
//
//  Created by zhuyc on 13-8-7.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKModelBaseElement.h"

// ================================================================================================
//  KKModelObject
//  Note:   base class, all our class should inherit from this class
// ================================================================================================
@implementation KKModelObject 

@end

// ================================================================================================
//  KKModelRspHeader
// ================================================================================================
@implementation KKModelRspHeader

@synthesize code = _code;
@synthesize request = _request;
@synthesize msgCode = _msgCode;
@synthesize desc = _desc;

-(void) dealloc
{
	self.request = nil;
	self.desc = nil;
	
	[super dealloc];
}

@end


// ================================================================================================
// ODB信息
// ================================================================================================
@implementation KKModelObdDetailInfo

- (void)dealloc
{
    self.obdSN = nil;
    self.obdId = nil;
    [super dealloc];
}

@end

// ================================================================================================
// ODB绑定的车辆信息
// ================================================================================================
@implementation KKModelObdInfo
@synthesize KKReservedKeyExt(default), vehicleVin, vehicleModelId, vehicleBrandId;

-(void) dealloc
{
    self.vehicleVin = nil;
    self.vehicleModelId = nil;
    self.vehicleBrandId = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// app端系统配置信息
// ================================================================================================
@implementation KKModelAppConfig 
@synthesize obdReadInterval, serverReadInterval, mileageInformInterval, customerServicePhone;

-(void) dealloc
{
    self.customerServicePhone = nil;
    self.remainOilMassWarn = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// 车辆基本信息
// ================================================================================================
@implementation KKModelVehicleInfo

-(void) dealloc
{
    self.vehicleVin = nil;
    self.vehicleBrand = nil;
    self.vehicleBrandId = nil;
    self.vehicleModel = nil;
    self.vehicleBrandId = nil;
    self.vehicleNo = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 品牌或车型
// ================================================================================================
@implementation KKModelBrandModel
-(void) dealloc
{
    self.name = nil;
    self.id = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 车辆车型信息
// ================================================================================================
@implementation KKModelCarInfo
- (void)dealloc
{
    self.brandName = nil;
    self.brandId = nil;
    self.modelName = nil;
    self.modelId = nil;
    [super dealloc];
}

@end

@implementation KKModelVehicleDetailInfo
- (void)dealloc
{
    self.status = nil;
    self.appUserId = nil;
    self.userNo = nil;
    self.mobile = nil;
    self.obdSN = nil;
    self.vehicleVin = nil;
    self.vehicleNo = nil;
    self.vehicleModel = nil;
    self.vehicleModelId = nil;
    self.vehicleBrand = nil;
    self.vehicleBrandId = nil;
    self.vehicleId = nil;
    self.nextMaintainMileage = nil;
    self.nextInsuranceTime = nil;
    self.nextExamineTime = nil;
    self.currentMileage = nil;
    self.email = nil;
    self.contact = nil;
    self.nextMaintainTime = nil;
    self.oilWear = nil;
    self.reportTime = nil;
    self.engineNo = nil;
    self.instantOilWear = nil;
    self.oilWearPerHundred = nil;
    self.oilMass = nil;
    self.engineCoolantTemperature = nil;
    self.batteryVoltage = nil;
    self.isDefault = nil;
    self.recommendShopName = nil;
    self.recommendShopId = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 店铺信息
// ================================================================================================

@implementation KKModelShopInfo

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.serviceScope = nil;
    self.coordinate = nil;
    self.smallImageUrl = nil;
    self.bigImageUrl = nil;
    self.address = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// 分页信息
// ================================================================================================

@implementation KKModelPagerInfo

@end


// ================================================================================================
// 地区信息
// ================================================================================================

@implementation KKModelAreaInfo

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.cityCode = nil;
    
    [super dealloc];
}

@end



// ================================================================================================
// 会员卡中购买的几次服务列表
// ================================================================================================
@implementation KKModelMemberService 

- (void)dealloc
{
    self.serviceId = nil;
    self.consumeType = nil;
    self.serviceName = nil;
    self.vehicles = nil;
    self.status = nil;
    self.deadlineStr = nil;
    self.timesStr = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 会员信息
// ================================================================================================
@implementation KKModelMemberInfo

- (void)dealloc
{
    self.type = nil;
    self.status = nil;
    self.KKArrayFieldName(memberServiceList, KKModelMemberService) = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 店铺详情服务范围
// ================================================================================================
@implementation KKModelShopServiceScope

- (void)dealloc
{
    self.serviceCategoryName = nil;
    self.shopId = nil;
    self.serviceCategoryId = nil;
    self.deleted = nil;
    self.id = nil;
    self.idStr = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 店铺详细信息
// ================================================================================================
@implementation KKModelShopDetail 

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.serviceScope = nil;
    self.coordinate = nil;
    self.imageUrl = nil;
    self.mobile = nil;
    self.landLine = nil;
    self.address = nil;
    self.memberInfo = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// 字典错误码
// ================================================================================================
@implementation KKModelFaultCodeInfo

- (void)dealloc
{
    self.faultCode = nil;
    self.description = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 消息
// ================================================================================================
@implementation KKModelMessage

- (void)dealloc
{
    self.id = nil;
    self.type = nil;
    self.content = nil;
    self.actionType = nil;
    self.params = nil;
    self.title = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 服务信息
// ================================================================================================
@implementation KKModelService

- (void)dealloc
{
    self.shopId = nil;
    self.shopName = nil;
    self.shopImageUrl = nil;
    self.content = nil;
    self.status = nil;
    self.orderId = nil;
    self.orderType = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 单据项
// ================================================================================================
@implementation KKModelOrderItem
- (void)dealloc
{
    self.content = nil;
    self.type = nil;
    [super dealloc];
}
@end

// ================================================================================================
// 结算信息
// ================================================================================================
@implementation KKModelSettleAccounts: KKModelObject

@end

// ================================================================================================
// 评价信息
// ================================================================================================
@implementation KKModelComment

- (void)dealloc
{
    self.commentContent = nil;
    
    [super dealloc];
}

@end


// ================================================================================================
// 服务单据详情
// ================================================================================================
@implementation KKModelserviceDetail: KKModelObject

- (void)dealloc
{
    self.id = nil;
    self.receiptNo = nil;
    self.status = nil;
    self.vehicleNo = nil;
    self.customerName = nil;
    self.shopId = nil;
    self.shopName = nil;
    self.serviceType = nil;
    self.orderType = nil;
    self.KKArrayFieldName(orderItems, KKModelOrderItem) = nil;
    self.settleAccounts = nil;
    self.comment = nil;
    self.actionType = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 个人资料
// ================================================================================================
@implementation KKModelUserInfo

- (void)dealloc
{
    self.userNo = nil;
    self.mobile = nil;
    self.name = nil;
    
    [super dealloc];
}

@end

// ================================================================================================
// 服务范围类别
// ================================================================================================

@implementation KKModelServiceCategory

- (void)dealloc
{
    self.id = nil;
    self.name = nil;
    self.parentId = nil;
    self.categoryType = nil;
    self.seviceScope = nil;
    [super dealloc];
}

@end

// ================================================================================================
// 平台信息
// ================================================================================================

@implementation KKModelPlatform

- (void)dealloc
{
    self.platform = nil;
    self.platformVersion = nil;
    self.mobileModel = nil;
    self.appVersion = nil;
    self.imageVersion = nil;
    [super dealloc];
}

@end

