/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.core.web.mvc.factory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hoteia.qalingo.core.Constants;
import org.hoteia.qalingo.core.domain.AbstractAttribute;
import org.hoteia.qalingo.core.domain.AbstractCatalog;
import org.hoteia.qalingo.core.domain.AbstractCatalogCategory;
import org.hoteia.qalingo.core.domain.AbstractPaymentGateway;
import org.hoteia.qalingo.core.domain.AbstractRuleReferential;
import org.hoteia.qalingo.core.domain.Asset;
import org.hoteia.qalingo.core.domain.AttributeDefinition;
import org.hoteia.qalingo.core.domain.BatchProcessObject;
import org.hoteia.qalingo.core.domain.CatalogCategoryMaster;
import org.hoteia.qalingo.core.domain.CatalogCategoryVirtual;
import org.hoteia.qalingo.core.domain.Company;
import org.hoteia.qalingo.core.domain.Customer;
import org.hoteia.qalingo.core.domain.DeliveryMethod;
import org.hoteia.qalingo.core.domain.EngineSetting;
import org.hoteia.qalingo.core.domain.EngineSettingValue;
import org.hoteia.qalingo.core.domain.Localization;
import org.hoteia.qalingo.core.domain.Market;
import org.hoteia.qalingo.core.domain.MarketArea;
import org.hoteia.qalingo.core.domain.MarketPlace;
import org.hoteia.qalingo.core.domain.OrderPurchase;
import org.hoteia.qalingo.core.domain.PaymentGatewayAttribute;
import org.hoteia.qalingo.core.domain.PaymentGatewayOption;
import org.hoteia.qalingo.core.domain.ProductAssociationLink;
import org.hoteia.qalingo.core.domain.ProductMarketing;
import org.hoteia.qalingo.core.domain.ProductSku;
import org.hoteia.qalingo.core.domain.Retailer;
import org.hoteia.qalingo.core.domain.Store;
import org.hoteia.qalingo.core.domain.Tax;
import org.hoteia.qalingo.core.domain.User;
import org.hoteia.qalingo.core.domain.Warehouse;
import org.hoteia.qalingo.core.domain.enumtype.BoUrls;
import org.hoteia.qalingo.core.domain.enumtype.FoUrls;
import org.hoteia.qalingo.core.fetchplan.FetchPlan;
import org.hoteia.qalingo.core.i18n.BoMessageKey;
import org.hoteia.qalingo.core.i18n.enumtype.I18nKeyValueUniverse;
import org.hoteia.qalingo.core.i18n.enumtype.ScopeCommonMessage;
import org.hoteia.qalingo.core.i18n.enumtype.ScopeReferenceDataMessage;
import org.hoteia.qalingo.core.i18n.enumtype.ScopeWebMessage;
import org.hoteia.qalingo.core.pojo.RequestData;
import org.hoteia.qalingo.core.service.BackofficeUrlService;
import org.hoteia.qalingo.core.web.mvc.viewbean.AssetViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.AttributeDefinitionViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.AttributeValueViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.BatchViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.CatalogCategoryViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.CatalogViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.CommonViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.CompanyViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.CustomerViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.DeliveryMethodViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.EngineSettingValueViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.EngineSettingViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.MenuViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.GlobalSearchViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.LegalTermsViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.LocalizationViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.MarketAreaViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.MarketViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.OrderViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.PaymentGatewayViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.ProductAssociationLinkViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.ProductMarketingViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.ProductSkuViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.RetailerViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.RuleViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.SecurityViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.SeoDataViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.StoreViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.TaxViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.UserViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.WarehouseViewBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 */
@Service("backofficeViewBeanFactory")
public class BackofficeViewBeanFactory extends ViewBeanFactory {

    @Autowired
    protected BackofficeUrlService backofficeUrlService;

    @Override
    public CommonViewBean buildViewBeanCommon(final RequestData requestData) throws Exception {
        final CommonViewBean commonViewBean = super.buildViewBeanCommon(requestData);

        commonViewBean.setHomeUrl(backofficeUrlService.generateUrl(BoUrls.HOME, requestData));
        commonViewBean.setLoginUrl(backofficeUrlService.generateUrl(BoUrls.LOGIN, requestData));
        commonViewBean.setForgottenPasswordUrl(backofficeUrlService.generateUrl(BoUrls.FORGOTTEN_PASSWORD, requestData));
        commonViewBean.setLogoutUrl(backofficeUrlService.generateUrl(BoUrls.LOGOUT, requestData));
        commonViewBean.setPersonalDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PERSONAL_DETAILS, requestData));

        commonViewBean.setContextJsonUrl(backofficeUrlService.generateUrl(BoUrls.CONTEXT, requestData));

        commonViewBean.setCurrentBackofficeLocalization(buildViewBeanLocalization(requestData, requestData.getBackofficeLocalization()));
        
        return commonViewBean;
    }
    
    @Override
    public SeoDataViewBean buildViewSeoData(final RequestData requestData) throws Exception {
        final HttpServletRequest request = requestData.getRequest();
        final Locale locale = requestData.getLocale();

        final SeoDataViewBean seoDataViewBean = super.buildViewSeoData(requestData);

        // TODO : canonical urls
        
        String pageTitle = getCommonMessage(ScopeCommonMessage.SEO, BoMessageKey.SEO_PAGE_TITLE_SITE_NAME, locale);
        seoDataViewBean.setPageTitle(pageTitle);
        String metaAuthor = getCommonMessage(ScopeCommonMessage.SEO, BoMessageKey.SEO_META_AUTHOR, locale);
        seoDataViewBean.setMetaAuthor(metaAuthor);
        String metaKeywords = getCommonMessage(ScopeCommonMessage.SEO, BoMessageKey.SEO_META_KEYWORDS, locale);
        seoDataViewBean.setMetaKeywords(metaKeywords);
        String metaDescription = getCommonMessage(ScopeCommonMessage.SEO, BoMessageKey.SEO_META_DESCRIPTION, locale);
        seoDataViewBean.setMetaDescription(metaDescription);

        String metaOgTitle = getSpecificMessage(ScopeWebMessage.SEO, BoMessageKey.PAGE_META_OG_TITLE, locale);
        seoDataViewBean.setMetaOgTitle(metaOgTitle);
        String metaOgDescription = getSpecificMessage(ScopeWebMessage.SEO, BoMessageKey.PAGE_META_OG_DESCRIPTION, locale);
        seoDataViewBean.setMetaOgDescription(metaOgDescription);
        String metaOgImage = getSpecificMessage(ScopeWebMessage.SEO, BoMessageKey.PAGE_META_OG_IMAGE, locale);
        seoDataViewBean.setMetaOgImage(metaOgImage);
        
        return seoDataViewBean;
    }
    
    public List<MenuViewBean> buildListViewBeanHeaderNav(final RequestData requestData) throws Exception {
        List<MenuViewBean> menuViewBeans = new ArrayList<MenuViewBean>();
        return menuViewBeans;
    }

    
    public List<MenuViewBean> buildListViewBeanHeaderMenu(final RequestData requestData) throws Exception {
        List<MenuViewBean> menuViewBeans = new ArrayList<MenuViewBean>();
        return menuViewBeans;
    }
    
    public List<MenuViewBean> buildListViewBeanMorePageMenu(final RequestData requestData) throws Exception {
        final HttpServletRequest request = requestData.getRequest();
        final User currentUser = requestData.getUser();
        final Locale locale = requestData.getLocale();

        final String currentUrl = requestUtil.getCurrentRequestUrl(request);
        
        final List<MenuViewBean> menuViewBeans = new ArrayList<MenuViewBean>();

        // TODO : denis : move this part in the global list menu java/vm

        int ordering = 1;
        
        MenuViewBean menuViewBean = new MenuViewBean();
        menuViewBean.setCssIcon("fa fa-question");
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "faq", locale));
        menuViewBean.setUrl(backofficeUrlService.generateUrl(BoUrls.FAQ, requestData));
        menuViewBean.setActive(currentUrl.contains(BoUrls.FAQ.getUrlPatternKey()));
        menuViewBean.setOrdering(ordering++);
        menuViewBeans.add(menuViewBean);

        menuViewBean = new MenuViewBean();
        menuViewBean.setCssIcon("fa fa-user");
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.COMMON, "header_link_my_account", locale));
        menuViewBean.setUrl(backofficeUrlService.generateUrl(BoUrls.PERSONAL_DETAILS, requestData, currentUser));
        menuViewBean.setActive(currentUrl.contains(BoUrls.PERSONAL_DETAILS.getUrlPatternKey()));
        menuViewBean.setOrdering(ordering++);
        menuViewBeans.add(menuViewBean);
        
        menuViewBean = new MenuViewBean();
        menuViewBean.setCssIcon("fa fa-lock");
        if(currentUser != null){
            menuViewBean.setName(getSpecificMessage(ScopeWebMessage.AUTH, "header_title_logout", locale));
            menuViewBean.setUrl(backofficeUrlService.generateUrl(BoUrls.LOGOUT, requestData));
            menuViewBean.setOrdering(ordering++);
            menuViewBean.setActive(currentUrl.contains(BoUrls.LOGOUT.getUrlPatternKey()));
        } else {
            menuViewBean.setName(getSpecificMessage(ScopeWebMessage.AUTH, "header_title_login", locale));
            menuViewBean.setUrl(backofficeUrlService.generateUrl(BoUrls.LOGIN, requestData));
            menuViewBean.setOrdering(ordering++);
            menuViewBean.setActive(currentUrl.contains(BoUrls.LOGIN.getUrlPatternKey()));
        }
        menuViewBeans.add(menuViewBean);
        
        return menuViewBeans;
    }

    /**
     * 
     */
    public List<MenuViewBean> buildListViewBeanFooterMenu(final RequestData requestData) throws Exception {
        final Locale locale = requestData.getLocale();
        List<MenuViewBean> MenuViewBeans = new ArrayList<MenuViewBean>();

        String MENU_TYPE_CUSTOMER_CARE = "MENU_TYPE_CUSTOMER_CARE";
        String MENU_TYPE_OUR_COMPANY   = "MENU_TYPE_OUR_COMPANY";
        
        int ordering = 1;
        
        MenuViewBean menuViewBean = new MenuViewBean();
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "conditionsofuse", locale));
        menuViewBean.setUrl(urlService.generateUrl(FoUrls.CONDITIONS_OF_USE, requestData));
        menuViewBean.setType(MENU_TYPE_CUSTOMER_CARE);
        menuViewBean.setOrdering(ordering++);
        MenuViewBeans.add(menuViewBean);

        menuViewBean = new MenuViewBean();
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "faq", locale));
        menuViewBean.setUrl(urlService.generateUrl(FoUrls.FAQ, requestData));
        menuViewBean.setType(MENU_TYPE_CUSTOMER_CARE);
        menuViewBean.setOrdering(ordering++);
        MenuViewBeans.add(menuViewBean);

        menuViewBean = new MenuViewBean();
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "legal_terms", locale));
        menuViewBean.setUrl(urlService.generateUrl(FoUrls.LEGAL_TERMS, requestData));
        menuViewBean.setType(MENU_TYPE_OUR_COMPANY);
        menuViewBean.setOrdering(ordering++);
        MenuViewBeans.add(menuViewBean);
        
        menuViewBean = new MenuViewBean();
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "contactus", locale));
        menuViewBean.setUrl(urlService.generateUrl(FoUrls.CONTACT, requestData));
        menuViewBean.setType(MENU_TYPE_OUR_COMPANY);
        menuViewBean.setOrdering(ordering++);
        MenuViewBeans.add(menuViewBean);

        menuViewBean = new MenuViewBean();
        menuViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "followus", locale));
        menuViewBean.setUrl(urlService.generateUrl(FoUrls.FOLLOW_US, requestData));
        menuViewBean.setType(MENU_TYPE_OUR_COMPANY);
        menuViewBean.setOrdering(ordering++);
        MenuViewBeans.add(menuViewBean);

        return MenuViewBeans;
    }
    
    /**
     * 
     */
    public List<MenuViewBean> buildListViewBeanFooterNav(final RequestData requestData) throws Exception {
        final Locale locale = requestData.getLocale();
        List<MenuViewBean> MenuViewBeans = new ArrayList<MenuViewBean>();

        String MENU_TYPE_CUSTOMER_CARE = "MENU_TYPE_CUSTOMER_CARE";
        String MENU_TYPE_OUR_COMPANY   = "MENU_TYPE_OUR_COMPANY";
        String MENU_TYPE_PRODUCT       = "MENU_TYPE_PRODUCT";
        String MENU_TYPE_MORE          = "MENU_TYPE_MORE";
        
        MenuViewBean footerMenuList = new MenuViewBean();
        footerMenuList.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "conditionsofuse", locale));
        footerMenuList.setUrl(urlService.generateUrl(FoUrls.CONDITIONS_OF_USE, requestData));
        footerMenuList.setType(MENU_TYPE_CUSTOMER_CARE);
        MenuViewBeans.add(footerMenuList);

        footerMenuList = new MenuViewBean();
        footerMenuList.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "faq", locale));
        footerMenuList.setUrl(urlService.generateUrl(FoUrls.FAQ, requestData));
        footerMenuList.setType(MENU_TYPE_CUSTOMER_CARE);
        MenuViewBeans.add(footerMenuList);

        footerMenuList = new MenuViewBean();
        footerMenuList.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "legal_terms", locale));
        footerMenuList.setUrl(urlService.generateUrl(FoUrls.LEGAL_TERMS, requestData));
        footerMenuList.setType(MENU_TYPE_OUR_COMPANY);
        MenuViewBeans.add(footerMenuList);
        
        footerMenuList = new MenuViewBean();
        footerMenuList.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "contactus", locale));
        footerMenuList.setUrl(urlService.generateUrl(FoUrls.CONTACT, requestData));
        footerMenuList.setType(MENU_TYPE_OUR_COMPANY);
        MenuViewBeans.add(footerMenuList);

        footerMenuList = new MenuViewBean();
        footerMenuList.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "followus", locale));
        footerMenuList.setUrl(urlService.generateUrl(FoUrls.FOLLOW_US, requestData));
        footerMenuList.setType(MENU_TYPE_OUR_COMPANY);
        MenuViewBeans.add(footerMenuList);
        
        return MenuViewBeans;
    }
    
    public List<MarketViewBean> buildListViewBeanMarketByMarketPlace(final RequestData requestData, final MarketPlace marketPlace, final List<Market> markets) throws Exception {
        List<MarketViewBean> marketViewBeans = new ArrayList<MarketViewBean>();
        for (Iterator<Market> iteratorMarket = markets.iterator(); iteratorMarket.hasNext();) {
            final Market marketNavigation = (Market) iteratorMarket.next();
            marketViewBeans.add(buildViewBeanMarket(requestData, marketNavigation));
        }
        return marketViewBeans;
    }

    public List<MarketAreaViewBean> buildListViewBeanMarketAreaByMarket(final RequestData requestData, final Market market, final List<MarketArea> marketAreas) throws Exception {
        List<MarketAreaViewBean> marketAreaViewBeans = new ArrayList<MarketAreaViewBean>();
        for (Iterator<MarketArea> iteratorMarketArea = marketAreas.iterator(); iteratorMarketArea.hasNext();) {
            final MarketArea marketArea = (MarketArea) iteratorMarketArea.next();
            marketAreaViewBeans.add(buildViewBeanMarketArea(requestData, marketArea));
        }
        return marketAreaViewBeans;
    }

    public LocalizationViewBean buildViewBeanLocalizationByMarketArea(final RequestData requestData, final Localization localization) throws Exception {
        final Locale locale = localization.getLocale();
        final String localeCodeNavigation = localization.getCode();

        RequestData requestDataChangecontext = new RequestData();
        BeanUtils.copyProperties(requestData, requestDataChangecontext);
        requestDataChangecontext.setMarketAreaLocalization(localization);

        final LocalizationViewBean localizationViewBean = new LocalizationViewBean();

        if (StringUtils.isNotEmpty(localeCodeNavigation) && localeCodeNavigation.length() == 2) {
            localizationViewBean.setName(getReferenceData(ScopeReferenceDataMessage.LANGUAGE, localeCodeNavigation.toLowerCase(), locale));
        } else {
            localizationViewBean.setName(getReferenceData(ScopeReferenceDataMessage.LANGUAGE, localeCodeNavigation, locale));
        }

        localizationViewBean.setChangeContextUrl(backofficeUrlService.buildChangeContextUrl(requestDataChangecontext));

        return localizationViewBean;
    }

    public List<LocalizationViewBean> buildListViewBeanLocalizationByMarketArea(final RequestData requestData, final List<Localization> localizations) throws Exception {
        final List<LocalizationViewBean> localizationViewBeans = new ArrayList<LocalizationViewBean>();
        if (localizations != null) {
            for (Iterator<Localization> iterator = localizations.iterator(); iterator.hasNext();) {
                Localization localization = (Localization) iterator.next();
                localizationViewBeans.add(buildViewBeanLocalizationByMarketArea(requestData, localization));
            }
        }
        return localizationViewBeans;
    }

    public List<LocalizationViewBean> buildListViewBeanLocalization(final RequestData requestData, final List<Localization> localizations) throws Exception {
        final List<LocalizationViewBean> localizationViewBeans = new ArrayList<LocalizationViewBean>();
        if (localizations != null) {
            for (Iterator<Localization> iterator = localizations.iterator(); iterator.hasNext();) {
                Localization localization = (Localization) iterator.next();
                localizationViewBeans.add(buildViewBeanLocalization(requestData, localization));
            }
        }
        return localizationViewBeans;
    }
    
    public List<RetailerViewBean> buildListViewBeanRetailer(final RequestData requestData) throws Exception {
        final MarketArea currentMarketArea = requestData.getMarketArea();
        final List<Retailer> retailers = new ArrayList<Retailer>(currentMarketArea.getRetailers());
        List<RetailerViewBean> retailerViewBeans = new ArrayList<RetailerViewBean>();
        for (Iterator<Retailer> iterator = retailers.iterator(); iterator.hasNext();) {
            final Retailer retailerIt = (Retailer) iterator.next();
            retailerViewBeans.add(buildViewBeanRetailer(requestData, retailerIt));
        }
        return retailerViewBeans;
    }

    @Override
    public RetailerViewBean buildViewBeanRetailer(final RequestData requestData, final Retailer retailer) throws Exception {
        final RetailerViewBean retailerViewBean = super.buildViewBeanRetailer(requestData, retailer);

        retailerViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.RETAILER_DETAILS, requestData, retailer));
        retailerViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.RETAILER_EDIT, requestData, retailer));
        retailerViewBean.setStoreListUrl(backofficeUrlService.generateUrl(BoUrls.STORE_LIST, requestData, retailer));

        return retailerViewBean;
    }

    @Override
    public StoreViewBean buildViewBeanStore(RequestData requestData, Store store) throws Exception {
        final StoreViewBean storeViewBean = super.buildViewBeanStore(requestData, store);
        
        storeViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.STORE_DETAILS, requestData, store));
        storeViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.STORE_EDIT, requestData, store));
        
        return storeViewBean;
    }
    
//    public CatalogViewBean buildViewBeanMasterCatalog(final RequestData requestData, final AbstractCatalog catalogMaster, final List<AbstractCatalogCategory> catalogCategories) throws Exception {
//        final CatalogViewBean catalogViewBean = new CatalogViewBean();
//        catalogViewBean.setName(catalogMaster.getName());
//        catalogViewBean.setCode(catalogMaster.getCode());
//
//        if (catalogCategories != null) {
//            final List<CatalogCategoryViewBean> catalogCategoryViewBeans = new ArrayList<CatalogCategoryViewBean>();
//            for (AbstractCatalogCategory catalogCategoryMaster : catalogCategories) {
//                CatalogCategoryViewBean catalogCategoryViewBean = buildViewBeanCatalogCategory(requestData, catalogCategoryMaster, true, false);
//                catalogCategoryViewBeans.add(catalogCategoryViewBean);
//            }
//            catalogViewBean.setCategories(catalogCategoryViewBeans);
//        }
//
//        catalogViewBean.setAddRootCategoryUrl(backofficeUrlService.generateUrl(BoUrls.MASTER_CATEGORY_ADD, requestData));
//
//        return catalogViewBean;
//    }

    public CatalogViewBean buildViewBeanCatalog(final RequestData requestData, final AbstractCatalog catalog, final List<AbstractCatalogCategory> catalogCategories, final FetchPlan categoryFetchPlan) throws Exception {
        final CatalogViewBean catalogViewBean = new CatalogViewBean();
        catalogViewBean.setName(catalog.getName());
        catalogViewBean.setCode(catalog.getCode());

        if (catalogCategories != null) {
            final List<CatalogCategoryViewBean> catalogCategoryViewBeans = new ArrayList<CatalogCategoryViewBean>();
            for (AbstractCatalogCategory catalogCategoryVirtual : catalogCategories) {
                CatalogCategoryViewBean catalogCategoryViewBean = buildViewBeanCatalogCategory(requestData, catalogCategoryVirtual);
                catalogCategoryViewBeans.add(catalogCategoryViewBean);
            }
            catalogViewBean.setCategories(catalogCategoryViewBeans);
        }

        catalogViewBean.setAddRootCategoryUrl(backofficeUrlService.generateUrl(BoUrls.VIRTUAL_CATEGORY_ADD, requestData));

        return catalogViewBean;
    }

//    public List<CatalogCategoryViewBean> buildListViewBeanMasterCatalogCategory(final RequestData requestData, final List<AbstractCatalogCategory> catalogCategories, boolean fullPopulate) throws Exception {
//        List<CatalogCategoryViewBean> categoryViewBeans = new ArrayList<CatalogCategoryViewBean>();
//        for (Iterator<AbstractCatalogCategory> iterator = catalogCategories.iterator(); iterator.hasNext();) {
//            final AbstractCatalogCategory catalogCategory = (AbstractCatalogCategory) iterator.next();
//
//            // TODO : Denis : fetch optim - cache : we reload entity to fetch the defaultParentCatalogCategory
//            AbstractCatalogCategory reloadedCategory = catalogCategoryService.getMasterCatalogCategoryById(catalogCategory.getId(), FetchPlanGraphCategory.masterCategoriesWithoutProductsAndAssetsFetchPlan());
//
//            categoryViewBeans.add(buildViewBeanMasterCatalogCategory(requestData, reloadedCategory, fullPopulate));
//        }
//        return categoryViewBeans;
//    }

//    public List<CatalogCategoryViewBean> buildListViewBeanVirtualCatalogCategory(final RequestData requestData, final List<CatalogCategoryVirtual> catalogCategories, boolean fullPopulate)
//            throws Exception {
//        List<CatalogCategoryViewBean> categoryViewBeans = new ArrayList<CatalogCategoryViewBean>();
//        for (Iterator<CatalogCategoryVirtual> iterator = catalogCategories.iterator(); iterator.hasNext();) {
//            final CatalogCategoryVirtual catalogCategory = (CatalogCategoryVirtual) iterator.next();
//            
//         // TODO : Denis : fetch optim - cache : we reload entity to fetch the defaultParentCatalogCategory
//            CatalogCategoryVirtual reloadedCategory = catalogCategoryService.getVirtualCatalogCategoryById(catalogCategory.getId(), FetchPlanGraphCategory.virtualCategoriesWithoutProductsAndAssetsFetchPlan());
//
//            categoryViewBeans.add(buildViewBeanVirtualCatalogCategory(requestData, reloadedCategory, fullPopulate));
//        }
//        return categoryViewBeans;
//    }

//    public CatalogCategoryViewBean buildViewBeanMasterCatalogCategory(final RequestData requestData, final AbstractCatalogCategory catalogCategory, boolean fullPopulate) throws Exception {
//        final MarketArea currentMarketArea = requestData.getMarketArea();
//        final CatalogCategoryViewBean catalogCategoryViewBean = new CatalogCategoryViewBean();
//
//        if (catalogCategory != null) {
//            final String categoryCode = catalogCategory.getCode();
//
//            catalogCategoryViewBean.setName(catalogCategory.getName());
//            catalogCategoryViewBean.setCode(categoryCode);
//            catalogCategoryViewBean.setDescription(catalogCategory.getDescription());
//
//            if (catalogCategory.getParentCatalogCategory() != null) {
//             // TODO : Denis : fetch optim - cache : we reload entity to fetch the defaultParentCatalogCategory
//                AbstractCatalogCategory defaultParentCatalogCategory = catalogCategoryService.getMasterCatalogCategoryById(catalogCategory.getParentCatalogCategory().getId(), FetchPlanGraphCategory.masterCategoriesWithoutProductsAndAssetsFetchPlan());
//                catalogCategoryViewBean.setDefaultParentCategory(buildViewBeanMasterCatalogCategory(requestData, defaultParentCatalogCategory, false));
//            }
//
//            DateFormat dateFormat = requestUtil.getFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
//            Date createdDate = catalogCategory.getDateCreate();
//            if (createdDate != null) {
//                catalogCategoryViewBean.setDateCreate(dateFormat.format(createdDate));
//            } else {
//                catalogCategoryViewBean.setDateCreate(Constants.NOT_AVAILABLE);
//            }
//            Date updatedDate = catalogCategory.getDateUpdate();
//            if (updatedDate != null) {
//                catalogCategoryViewBean.setDateUpdate(dateFormat.format(updatedDate));
//            } else {
//                catalogCategoryViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
//            }
//
//            if (fullPopulate) {
//                if (catalogCategory.getCatalogCategories() != null) {
//                    catalogCategoryViewBean.setSubCategories(buildListViewBeanMasterCatalogCategory(requestData, new ArrayList<AbstractCatalogCategory>(catalogCategory.getCatalogCategories()), fullPopulate));
//                }
//
//                List<AbstractCatalogCategoryAttribute> globalAttributes = catalogCategory.getCatalogCategoryGlobalAttributes();
//                for (Iterator<AbstractCatalogCategoryAttribute> iterator = globalAttributes.iterator(); iterator.hasNext();) {
//                    AbstractCatalogCategoryAttribute catalogCategoryMasterAttribute = (AbstractCatalogCategoryAttribute) iterator.next();
//                    catalogCategoryViewBean.getGlobalAttributes().put(catalogCategoryMasterAttribute.getAttributeDefinition().getCode(), catalogCategoryMasterAttribute.getValueAsString());
//                }
//
//                List<AbstractCatalogCategoryAttribute> marketAreaAttributes = catalogCategory.getCatalogCategoryMarketAreaAttributes(currentMarketArea.getId());
//                for (Iterator<AbstractCatalogCategoryAttribute> iterator = marketAreaAttributes.iterator(); iterator.hasNext();) {
//                    AbstractCatalogCategoryAttribute catalogCategoryMasterAttribute = (AbstractCatalogCategoryAttribute) iterator.next();
//                    catalogCategoryViewBean.getMarketAreaAttributes().put(catalogCategoryMasterAttribute.getAttributeDefinition().getCode(), catalogCategoryMasterAttribute.getValueAsString());
//                }
//
//                List<ProductMarketingViewBean> productMarketingViewBeans = buildListViewBeanProductMarketing(requestData, catalogCategory, new ArrayList<ProductMarketing>(catalogCategory.getProductMarketings()), true);
//                catalogCategoryViewBean.setProductMarketings(productMarketingViewBeans);
//
//                int countProduct = catalogCategory.getProductMarketings().size();
//                for (Iterator<CatalogCategoryViewBean> iterator = catalogCategoryViewBean.getSubCategories().iterator(); iterator.hasNext();) {
//                    CatalogCategoryViewBean subCategoryViewBean = (CatalogCategoryViewBean) iterator.next();
//                    countProduct = countProduct + subCategoryViewBean.getCountProduct();
//                }
//                catalogCategoryViewBean.setCountProduct(countProduct);
//
//                List<Asset> assets = catalogCategory.getAssetsIsGlobal();
//                for (Iterator<Asset> iterator = assets.iterator(); iterator.hasNext();) {
//                    Asset asset = (Asset) iterator.next();
//                    catalogCategoryViewBean.getAssets().add(buildViewBeanAsset(requestData, asset));
//                }
//            }
//
//            catalogCategoryViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.MASTER_CATEGORY_DETAILS, requestData, catalogCategory));
//            catalogCategoryViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.MASTER_CATEGORY_EDIT, requestData, catalogCategory));
//        }
//
//        return catalogCategoryViewBean;
//    }

//    public CatalogCategoryViewBean buildViewBeanVirtualCatalogCategory(final RequestData requestData, final CatalogCategoryVirtual catalogCategory, boolean fullPopulate) throws Exception {
//        final MarketArea currentMarketArea = requestData.getMarketArea();
//        final CatalogCategoryViewBean catalogCategoryViewBean = new CatalogCategoryViewBean();
//
//        if (catalogCategory != null) {
//            final String categoryCode = catalogCategory.getCode();
//
//            catalogCategoryViewBean.setName(catalogCategory.getName());
//            catalogCategoryViewBean.setCode(categoryCode);
//            catalogCategoryViewBean.setDescription(catalogCategory.getDescription());
//
//            if (catalogCategory.getParentCatalogCategory() != null) {
//                // TODO : Denis : fetch optim - cache : we reload entity to fetch the defaultParentCatalogCategory
//                CatalogCategoryVirtual defaultParentCatalogCategory = catalogCategoryService.getVirtualCatalogCategoryById(catalogCategory.getParentCatalogCategory().getId(), FetchPlanGraphCategory.virtualCategoriesWithoutProductsAndAssetsFetchPlan());
//                catalogCategoryViewBean.setDefaultParentCategory(buildViewBeanVirtualCatalogCategory(requestData, defaultParentCatalogCategory, false));
//            }
//
//            DateFormat dateFormat = requestUtil.getFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
//            Date createdDate = catalogCategory.getDateCreate();
//            if (createdDate != null) {
//                catalogCategoryViewBean.setDateCreate(dateFormat.format(createdDate));
//            } else {
//                catalogCategoryViewBean.setDateCreate(Constants.NOT_AVAILABLE);
//            }
//            Date updatedDate = catalogCategory.getDateUpdate();
//            if (updatedDate != null) {
//                catalogCategoryViewBean.setDateUpdate(dateFormat.format(updatedDate));
//            } else {
//                catalogCategoryViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
//            }
//
//            if (fullPopulate) {
//                if (catalogCategory.getSortedChildCatalogCategories() != null) {
//                    catalogCategoryViewBean.setSubCategories(buildListViewBeanVirtualCatalogCategory(requestData, new ArrayList<CatalogCategoryVirtual>(catalogCategory.getSortedChildCatalogCategories()), fullPopulate));
//                }
//
//                List<CatalogCategoryVirtualAttribute> globalAttributes = catalogCategory.getCatalogCategoryGlobalAttributes();
//                for (Iterator<CatalogCategoryVirtualAttribute> iterator = globalAttributes.iterator(); iterator.hasNext();) {
//                    CatalogCategoryVirtualAttribute catalogCategoryVirtualAttribute = (CatalogCategoryVirtualAttribute) iterator.next();
//                    catalogCategoryViewBean.getGlobalAttributes().put(catalogCategoryVirtualAttribute.getAttributeDefinition().getCode(), catalogCategoryVirtualAttribute.getValueAsString());
//                }
//
//                List<CatalogCategoryVirtualAttribute> marketAreaAttributes = catalogCategory.getCatalogCategoryMarketAreaAttributes(currentMarketArea.getId());
//                for (Iterator<CatalogCategoryVirtualAttribute> iterator = marketAreaAttributes.iterator(); iterator.hasNext();) {
//                    CatalogCategoryVirtualAttribute catalogCategoryVirtualAttribute = (CatalogCategoryVirtualAttribute) iterator.next();
//                    catalogCategoryViewBean.getMarketAreaAttributes().put(catalogCategoryVirtualAttribute.getAttributeDefinition().getCode(), catalogCategoryVirtualAttribute.getValueAsString());
//                }
//
//                new ArrayList<ProductMarketing>(catalogCategory.getProductMarketings())
//                List<ProductMarketingViewBean> productMarketingViewBeans = buildListViewBeanProductMarketing(requestData, catalogCategory, , true);
//                catalogCategoryViewBean.setProductMarketings(productMarketingViewBeans);
//
//                int countProduct = catalogCategory.getProductMarketings().size();
//                for (Iterator<CatalogCategoryViewBean> iterator = catalogCategoryViewBean.getSubCategories().iterator(); iterator.hasNext();) {
//                    CatalogCategoryViewBean subCategoryViewBean = (CatalogCategoryViewBean) iterator.next();
//                    countProduct = countProduct + subCategoryViewBean.getCountProduct();
//                }
//                catalogCategoryViewBean.setCountProduct(countProduct);
//
//                List<Asset> assets = catalogCategory.getAssetsIsGlobal();
//                for (Iterator<Asset> iterator = assets.iterator(); iterator.hasNext();) {
//                    Asset asset = (Asset) iterator.next();
//                    catalogCategoryViewBean.getAssets().add(buildViewBeanAsset(requestData, asset));
//                }
//            }
//
//            catalogCategoryViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.VIRTUAL_CATEGORY_DETAILS, requestData, catalogCategory));
//            catalogCategoryViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.VIRTUAL_CATEGORY_EDIT, requestData, catalogCategory));
//        }
//
//        return catalogCategoryViewBean;
//    }

    /**
     * 
     */
    @Override
    public CatalogCategoryViewBean buildViewBeanMasterCatalogCategory(final RequestData requestData, final CatalogCategoryMaster catalogCategory) throws Exception {
        final CatalogCategoryViewBean catalogCategoryViewBean = super.buildViewBeanMasterCatalogCategory(requestData, catalogCategory);

        catalogCategoryViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.MASTER_CATEGORY_DETAILS, requestData, catalogCategory));
        catalogCategoryViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.MASTER_CATEGORY_EDIT, requestData, catalogCategory));
        
        return catalogCategoryViewBean;
    }
    
    /**
     * 
     */
    @Override
    public CatalogCategoryViewBean buildViewBeanVirtualCatalogCategory(final RequestData requestData, final CatalogCategoryVirtual catalogCategory) throws Exception {
        final CatalogCategoryViewBean catalogCategoryViewBean = super.buildViewBeanVirtualCatalogCategory(requestData, catalogCategory);

        catalogCategoryViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.VIRTUAL_CATEGORY_DETAILS, requestData, catalogCategory));
        catalogCategoryViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.VIRTUAL_CATEGORY_EDIT, requestData, catalogCategory));
            
        return catalogCategoryViewBean;
    }
    
    public List<ProductMarketingViewBean> buildListViewBeanProductMarketing(final RequestData requestData, final AbstractCatalogCategory catalogCategory, final List<ProductMarketing> productMarketings, boolean withDependency) throws Exception {
        List<ProductMarketingViewBean> products = new ArrayList<ProductMarketingViewBean>();
        for (Iterator<ProductMarketing> iterator = productMarketings.iterator(); iterator.hasNext();) {
            ProductMarketing productMarketing = (ProductMarketing) iterator.next();
            
            // TODO : Denis : fetch optim - cache
            ProductMarketing reloadedProductMarketing = (ProductMarketing) productService.getProductMarketingById(productMarketing.getId());
            
            products.add(buildViewBeanProductMarketing(requestData, catalogCategory, reloadedProductMarketing, null));
//            products.add(buildProductMarketingViewBean(requestData, reloadedProductMarketing, withDependency));
        }
        return products;
    }

    /**
     * @throws Exception
     * 
     */
    public List<ProductMarketingViewBean> buildListViewBeanProductMarketing(final RequestData requestData, final CatalogCategoryVirtual catalogCategory, final List<ProductMarketing> productMarketings, boolean withDependency) throws Exception {
        List<ProductMarketingViewBean> products = new ArrayList<ProductMarketingViewBean>();
        for (Iterator<ProductMarketing> iterator = productMarketings.iterator(); iterator.hasNext();) {
            ProductMarketing productMarketing = (ProductMarketing) iterator.next();
            
            // TODO : Denis : fetch optim - cache
            ProductMarketing reloadedProductMarketing = (ProductMarketing) productService.getProductMarketingById(productMarketing.getId());
            
            products.add(buildViewBeanProductMarketing(requestData, catalogCategory, reloadedProductMarketing, null));
//            products.add(buildProductMarketingViewBean(requestData, reloadedProductMarketing, withDependency));
        }
        return products;
    }
    
//    public ProductMarketingViewBean buildViewBeanProductMarketing(final RequestData requestData, ProductMarketing productMarketing) throws Exception {
//        AbstractCatalogCategory catalogCategory = null;
//        if(!Hibernate.isInitialized(productMarketing.getDefaultCatalogCategory().getCategoryMaster())){
//            CatalogCategoryVirtual categoryVirtual = catalogCategoryService.getVirtualCatalogCategoryById(productMarketing.getDefaultCatalogCategory().getId(), FetchPlanGraphCategory.virtualCategoriesWithoutProductsAndAssetsFetchPlan());
//            catalogCategory = catalogCategoryService.getMasterCatalogCategoryById(categoryVirtual.getCategoryMaster().getId(), FetchPlanGraphCategory.masterCategoriesWithoutProductsAndAssetsFetchPlan());
//        } else {
//            catalogCategory = catalogCategoryService.getMasterCatalogCategoryById(productMarketing.getDefaultCatalogCategory().getCategoryMaster().getId(), FetchPlanGraphCategory.masterCategoriesWithoutProductsAndAssetsFetchPlan());
//        }
//        final ProductMarketingViewBean productMarketingViewBean = buildViewBeanProductMarketing(requestData, catalogCategory, productMarketing);
//        return productMarketingViewBean;
//    }

    @Override
    public ProductMarketingViewBean buildViewBeanProductMarketing(final RequestData requestData, final ProductMarketing productMarketing) throws Exception {
        final ProductMarketingViewBean productMarketingViewBean = super.buildViewBeanProductMarketing(requestData, productMarketing);

        productMarketingViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_MARKETING_DETAILS, requestData, productMarketing));
        productMarketingViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_MARKETING_EDIT, requestData, productMarketing));
        productMarketingViewBean.setAddSkuUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_EDIT, requestData, productMarketing));

        return productMarketingViewBean;
    }
    
    public ProductAssociationLinkViewBean buildProductAssociationLinkViewBean(final RequestData requestData, final ProductAssociationLink productAssociationLink) throws Exception {
        final ProductAssociationLinkViewBean productAssociationLinkViewBean = new ProductAssociationLinkViewBean();

        productAssociationLinkViewBean.setOrderItem(productAssociationLink.getRankPosition());
        productAssociationLinkViewBean.setName(productAssociationLink.getProductSku().getProductMarketing().getName());
        productAssociationLinkViewBean.setType(productAssociationLink.getType().name());
        productAssociationLinkViewBean.setDescription(productAssociationLink.getType().name());
        productAssociationLinkViewBean.setProductDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_MARKETING_DETAILS, requestData, productAssociationLink.getProductSku().getProductMarketing()));

        return productAssociationLinkViewBean;
    }
    
//    @Override
//    public ProductSkuViewBean buildViewBeanProductSku(final RequestData requestData, final ProductSku productSku) throws Exception {
//        ProductMarketing productMarketing = productSku.getProductMarketing();
//        if(!Hibernate.isInitialized(productMarketing.getDefaultCatalogCategory())){
//            productMarketing = productService.getProductMarketingById(productMarketing.getId(), FetchPlanGraphProduct.productMarketingBackofficeCatalogueViewFetchPlan());
//        }
//        AbstractCatalogCategory catalogCategory = null;
//        if(!Hibernate.isInitialized(productMarketing.getDefaultCatalogCategory().getCategoryMaster())){
//            CatalogCategoryVirtual categoryVirtual = catalogCategoryService.getVirtualCatalogCategoryById(productMarketing.getDefaultCatalogCategory().getId(), FetchPlanGraphCategory.virtualCategoriesWithoutProductsAndAssetsFetchPlan());
//            catalogCategory = catalogCategoryService.getMasterCatalogCategoryById(categoryVirtual.getCategoryMaster().getId(), FetchPlanGraphCategory.masterCategoriesWithoutProductsAndAssetsFetchPlan());
//        } else {
//            catalogCategory = catalogCategoryService.getMasterCatalogCategoryById(productMarketing.getDefaultCatalogCategory().getCategoryMaster().getId(), FetchPlanGraphCategory.masterCategoriesWithoutProductsAndAssetsFetchPlan());
//        }
//        final ProductSkuViewBean productSkuViewBean = buildViewBeanProductSku(requestData, catalogCategory, productMarketing, productSku);
//        return productSkuViewBean;
//    }
    
    @Override
    public ProductSkuViewBean buildViewBeanProductSku(final RequestData requestData, final AbstractCatalogCategory catalogCategory, 
                                                      final ProductMarketing productMarketing, final ProductSku productSku) throws Exception {
        final ProductSkuViewBean productSkuViewBean = buildViewBeanProductSku(requestData, productSku);

        productSkuViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_DETAILS, requestData, productSku));
        productSkuViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_EDIT, requestData, productSku));

        return productSkuViewBean;
    }

     @Override
     public ProductSkuViewBean buildViewBeanProductSku(final RequestData requestData, final ProductSku productSku) throws Exception {
        final ProductSkuViewBean productSkuViewBean = super.buildViewBeanProductSku(requestData, productSku);

        productSkuViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_DETAILS, requestData, productSku));
        productSkuViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_EDIT, requestData, productSku));

        return productSkuViewBean;
    }
     
//    /**
//     * @throws Exception
//     * 
//     */
//     @Override
//     public ProductSkuViewBean buildViewBeanProductSku(final RequestData requestData, final CatalogCategoryVirtual catalogCategory, 
//                                                      final ProductMarketing productMarketing, final ProductSku productSku) throws Exception {
//        final ProductSkuViewBean productSkuViewBean = super.buildViewBeanProductSku(requestData, catalogCategory, productMarketing, productSku);
//
//        productSkuViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_DETAILS, requestData, productSku));
//        productSkuViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_EDIT, requestData, productSku));
//
//        return productSkuViewBean;
//    }

//    /**
//     * 
//     */
//    public BrandViewBean buildBrandViewBean(final RequestData requestData, final ProductBrand productBrand) throws Exception {
//        final BrandViewBean brandViewBean = new BrandViewBean();
//
//        brandViewBean.setBusinessName(productBrand.getName());
//        brandViewBean.setCode(productBrand.getCode());
//        // brandViewBean.setBrandDetailsUrl(brandDetailsUrl);
//        // brandViewBean.setBrandLineDetailsUrl(brandLineDetailsUrl);
//
//        return brandViewBean;
//    }

    /**
     * @throws Exception
     * 
     */
     @Override
    public AssetViewBean buildViewBeanAsset(final RequestData requestData, final Asset asset) throws Exception {
        AssetViewBean assetViewBean = super.buildViewBeanAsset(requestData, asset);
        assetViewBean.setAbsoluteWebPath(engineSettingService.getProductMarketingImageWebPath(asset));

        DateFormat dateFormat = requestUtil.getCommonFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
        Date createdDate = asset.getDateCreate();
        if (createdDate != null) {
            assetViewBean.setDateCreate(dateFormat.format(createdDate));
        } else {
            assetViewBean.setDateCreate(Constants.NOT_AVAILABLE);
        }
        Date updatedDate = asset.getDateUpdate();
        if (updatedDate != null) {
            assetViewBean.setDateUpdate(dateFormat.format(updatedDate));
        } else {
            assetViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
        }

        if(!Asset.ASSET_TYPE_DEFAULT.equals(asset.getType())){
            assetViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.ASSET_DETAILS, requestData, asset));
            assetViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.ASSET_EDIT, requestData, asset));
        }

        return assetViewBean;
    }

    /**
     * @throws Exception
     * 
     */
    public LegalTermsViewBean buildViewBeanLegalTerms(final RequestData requestData) throws Exception {
        final Localization localization = requestData.getMarketAreaLocalization();
        final Locale locale = localization.getLocale();

        final LegalTermsViewBean legalTerms = new LegalTermsViewBean();

        legalTerms.setWarning(getCommonMessage(ScopeCommonMessage.LEGAL_TERMS, "warning", locale));
        legalTerms.setCopyright(getCommonMessage(ScopeCommonMessage.LEGAL_TERMS, "copyright", locale));

        return legalTerms;
    }

    /**
     * @throws Exception
     * 
     */
    public SecurityViewBean buildViewBeanSecurity(final RequestData requestData) throws Exception {
        final SecurityViewBean security = new SecurityViewBean();
        security.setLoginUrl(backofficeUrlService.generateUrl(BoUrls.LOGIN, requestData));
        security.setSubmitLoginUrl(backofficeUrlService.buildSpringSecurityCheckUrl(requestData));
        security.setForgottenPasswordUrl(backofficeUrlService.generateUrl(BoUrls.FORGOTTEN_PASSWORD, requestData));
        security.setResetPasswordUrl(backofficeUrlService.generateUrl(BoUrls.RESET_PASSWORD, requestData));
        security.setCreateAccountUrl(backofficeUrlService.generateUrl(BoUrls.USER_CREATE_ACCOUNT, requestData));
        return security;
    }

    /**
     * @throws Exception
     * 
     */
    @Override
    public UserViewBean buildViewBeanUser(final RequestData requestData, final User user) throws Exception {
        final UserViewBean userViewBean = super.buildViewBeanUser(requestData, user);

        userViewBean.setUserDetailsUrl(backofficeUrlService.generateUrl(BoUrls.USER_DETAILS, requestData, user));
        userViewBean.setUserEditUrl(backofficeUrlService.generateUrl(BoUrls.USER_EDIT, requestData, user));

        userViewBean.setPersonalDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PERSONAL_DETAILS, requestData, user));
        userViewBean.setPersonalEditUrl(backofficeUrlService.generateUrl(BoUrls.PERSONAL_EDIT, requestData, user));

        return userViewBean;
    }

    /**
     * @throws Exception
     * 
     */
    @Override
    public CompanyViewBean buildViewBeanCompany(final RequestData requestData, final Company company) throws Exception {
        final CompanyViewBean companyViewBean = super.buildViewBeanCompany(requestData, company);

        companyViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.COMPANY_DETAILS, requestData, company));
        companyViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.COMPANY_EDIT, requestData, company));

        return companyViewBean;
    }

    /**
     * @throws Exception
     * 
     */
    @Override
    public DeliveryMethodViewBean buildViewBeanDeliveryMethod(final RequestData requestData, final DeliveryMethod deliveryMethod) throws Exception {
        final DeliveryMethodViewBean deliveryMethodViewBean = super.buildViewBeanDeliveryMethod(requestData, deliveryMethod);

        deliveryMethodViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.DELIVERY_METHOD_DETAILS, requestData, deliveryMethod));
        deliveryMethodViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.DELIVERY_METHOD_EDIT, requestData, deliveryMethod));

        return deliveryMethodViewBean;
    }
    
    /**
     * @throws Exception
     * 
     */
    @Override
    public TaxViewBean buildViewBeanTax(final RequestData requestData, final Tax tax) throws Exception {
        final TaxViewBean taxViewBean = super.buildViewBeanTax(requestData, tax);

        taxViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.TAX_DETAILS, requestData, tax));
        taxViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.TAX_EDIT, requestData, tax));

        return taxViewBean;
    }    

    /**
     * @throws Exception
     * 
     */
    @Override
    public OrderViewBean buildViewBeanOrder(final RequestData requestData, final OrderPurchase order) throws Exception {
        OrderViewBean orderViewBean = super.buildViewBeanOrder(requestData, order);

        orderViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.ORDER_DETAILS, requestData, order));
        orderViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.ORDER_DETAILS, requestData, order));

        return orderViewBean;
    }

    /**
     * @throws Exception
     * 
     */
    public RuleViewBean buildViewBeanRule(final RequestData requestData, final AbstractRuleReferential rule) throws Exception {
        RuleViewBean ruleViewBean = new RuleViewBean();
        if(rule.getId() != null){
            ruleViewBean.setId(rule.getId().toString());
        }
        ruleViewBean.setVersion(rule.getVersion());

        ruleViewBean.setCode(rule.getCode());
        ruleViewBean.setName(rule.getName());
        ruleViewBean.setDescription(rule.getDescription());
        ruleViewBean.setSalience(rule.getSalience());

        DateFormat dateFormat = requestUtil.getCommonFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
        if (rule.getDateCreate() != null) {
            ruleViewBean.setDateCreate(dateFormat.format(rule.getDateCreate()));
        } else {
            ruleViewBean.setDateCreate(Constants.NOT_AVAILABLE);
        }
        if (rule.getDateUpdate() != null) {
            ruleViewBean.setDateUpdate(dateFormat.format(rule.getDateUpdate()));
        } else {
            ruleViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
        }

        ruleViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.RULE_DETAILS, requestData, rule));
        ruleViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.RULE_EDIT, requestData, rule));

        return ruleViewBean;
    }

    /**
     * @throws Exception
     * 
     */
    @Override
    public CustomerViewBean buildViewBeanCustomer(final RequestData requestData, final Customer customer) throws Exception {
        CustomerViewBean customerViewBean = super.buildViewBeanCustomer(requestData, customer);

        customerViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.CUSTOMER_DETAILS, requestData, customer));
        customerViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.CUSTOMER_EDIT, requestData, customer));

        return customerViewBean;
    }

    /**
     * @throws Exception
     * 
     */
    public List<GlobalSearchViewBean> buildListViewBeanGlobalSearch(final RequestData requestData, final String searchText) throws Exception {
        final MarketArea currentMarketArea = requestData.getMarketArea();

        final List<GlobalSearchViewBean> globalSearchViewBeans = new ArrayList<GlobalSearchViewBean>();

        final List<ProductMarketing> productMarketings = productService.findProductMarketings(currentMarketArea.getId(), searchText);
        for (Iterator<ProductMarketing> iterator = productMarketings.iterator(); iterator.hasNext();) {
            ProductMarketing productMarketing = (ProductMarketing) iterator.next();

            final GlobalSearchViewBean globalSearchViewBean = new GlobalSearchViewBean();
            globalSearchViewBean.setValue(productMarketing.getName() + " : " + productMarketing.getCode());
            globalSearchViewBean.setType("ProductMarketing");
            globalSearchViewBean.setUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_MARKETING_DETAILS, requestData, productMarketing));

            globalSearchViewBeans.add(globalSearchViewBean);
        }

        final List<ProductSku> productSkus = productService.findProductSkus(searchText);
        for (Iterator<ProductSku> iterator = productSkus.iterator(); iterator.hasNext();) {
            ProductSku productSku = (ProductSku) iterator.next();

            final GlobalSearchViewBean globalSearchViewBean = new GlobalSearchViewBean();
            globalSearchViewBean.setValue(productSku.getName() + " : " + productSku.getCode());
            globalSearchViewBean.setType("ProductSku");
            globalSearchViewBean.setUrl(backofficeUrlService.generateUrl(BoUrls.PRODUCT_SKU_DETAILS, requestData, productSku));

            globalSearchViewBeans.add(globalSearchViewBean);
        }

        return globalSearchViewBeans;
    }

    /**
     * 
     */
    public List<EngineSettingViewBean> buildListViewBeanEngineSetting(final RequestData requestData, final List<EngineSetting> engineSettings) throws Exception {
        final List<EngineSettingViewBean> engineSettingViewBeans = new ArrayList<EngineSettingViewBean>();
        for (Iterator<EngineSetting> iterator = engineSettings.iterator(); iterator.hasNext();) {
            EngineSetting engineSetting = (EngineSetting) iterator.next();
            engineSettingViewBeans.add(buildViewBeanEngineSetting(requestData, engineSetting));
        }
        return engineSettingViewBeans;
    }

    /**
     * 
     */
    public EngineSettingViewBean buildViewBeanEngineSetting(final RequestData requestData, final EngineSetting engineSetting) throws Exception {
        final EngineSettingViewBean engineSettingViewBean = new EngineSettingViewBean();
        engineSettingViewBean.setName(engineSetting.getName());
        engineSettingViewBean.setCode(engineSetting.getCode());
        engineSettingViewBean.setDescription(engineSetting.getDescription());
        if(StringUtils.isNotEmpty(engineSetting.getDefaultValue())){
            engineSettingViewBean.setDefaultValue(engineSetting.getDefaultValue());
        } else {
            engineSettingViewBean.setDefaultValue(Constants.NOT_AVAILABLE);
        }
        
        Set<EngineSettingValue> engineSettingValues = engineSetting.getEngineSettingValues();
        if (engineSettingValues != null) {
            for (Iterator<EngineSettingValue> iterator = engineSettingValues.iterator(); iterator.hasNext();) {
                EngineSettingValue engineSettingValue = (EngineSettingValue) iterator.next();
                engineSettingViewBean.getEngineSettingValues().add(buildViewBeanEngineSettingValue(requestData, engineSettingValue));
            }
        }
        
        DateFormat dateFormat = requestUtil.getCommonFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
        Date dateCreate = engineSetting.getDateCreate();
        if (dateCreate != null) {
            engineSettingViewBean.setDateCreate(dateFormat.format(dateCreate));
        } else {
            engineSettingViewBean.setDateCreate(Constants.NOT_AVAILABLE);
        }

        Date dateUpdate = engineSetting.getDateUpdate();
        if (dateUpdate != null) {
            engineSettingViewBean.setDateUpdate(dateFormat.format(dateUpdate));
        } else {
            engineSettingViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
        }

        engineSettingViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.ENGINE_SETTING_DETAILS, requestData, engineSetting));
        engineSettingViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.ENGINE_SETTING_EDIT, requestData, engineSetting));

        return engineSettingViewBean;
    }

    /**
     * 
     */
    public EngineSettingValueViewBean buildViewBeanEngineSettingValue(final RequestData requestData, final EngineSettingValue engineSettingValue) throws Exception {
        final EngineSettingValueViewBean engineSettingValueViewBean = new EngineSettingValueViewBean();
        engineSettingValueViewBean.setContext(engineSettingValue.getContext());
        engineSettingValueViewBean.setValue(engineSettingValue.getValue());
        engineSettingValueViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.ENGINE_SETTING_VALUE_EDIT, requestData, engineSettingValue));
        return engineSettingValueViewBean;
    }

    /**
     * 
     */
    public List<BatchViewBean> buildListViewBeanBatch(final RequestData requestData, final List<BatchProcessObject> batchProcessObjects) throws Exception {
        final List<BatchViewBean> batchViewBeans = new ArrayList<BatchViewBean>();
        for (Iterator<BatchProcessObject> iterator = batchProcessObjects.iterator(); iterator.hasNext();) {
            BatchProcessObject batchProcessObject = (BatchProcessObject) iterator.next();
            batchViewBeans.add(buildViewBeanBatch(requestData, batchProcessObject));
        }
        return batchViewBeans;
    }

    /**
     * 
     */
    public BatchViewBean buildViewBeanBatch(final RequestData requestData, final BatchProcessObject batchProcessObject) throws Exception {
        final BatchViewBean batchViewBean = new BatchViewBean();
        if(batchProcessObject.getId() != null){
            batchViewBean.setId(batchProcessObject.getId().toString());
        }
        batchViewBean.setStatus(batchProcessObject.getStatus());
        batchViewBean.setTypeObject(batchProcessObject.getTypeObject().getPropertyKey());
        batchViewBean.setProcessedCount(batchProcessObject.getProcessedCount());
        return batchViewBean;
    }

    /**
     * 
     */
    public List<WarehouseViewBean> buildListViewBeanWarehouse(final RequestData requestData, final List<Warehouse> warehouses) throws Exception {
        final List<WarehouseViewBean> warehouseViewBeans = new ArrayList<WarehouseViewBean>();
        if (warehouses != null) {
            for (Iterator<Warehouse> iterator = warehouses.iterator(); iterator.hasNext();) {
                Warehouse warehouse = (Warehouse) iterator.next();
                warehouseViewBeans.add(buildViewBeanWarehouse(requestData, warehouse));
            }
        }
        return warehouseViewBeans;
    }

    /**
     * 
     */
    public WarehouseViewBean buildViewBeanWarehouse(final RequestData requestData, final Warehouse warehouse) throws Exception {
        final WarehouseViewBean warehouseViewBean = new WarehouseViewBean();
        if (warehouse != null) {
            warehouseViewBean.setCode(warehouse.getCode());
            warehouseViewBean.setName(warehouse.getName());
            warehouseViewBean.setDescription(warehouse.getDescription());

            warehouseViewBean.setAddress1(warehouse.getAddress1());
            warehouseViewBean.setAddress2(warehouse.getAddress2());
            warehouseViewBean.setAddressAdditionalInformation(warehouse.getAddressAdditionalInformation());
            warehouseViewBean.setPostalCode(warehouse.getPostalCode());

            // I18n values
            warehouseViewBean.setCity(warehouse.getCity());
            warehouseViewBean.setStateCode(warehouse.getStateCode());
            warehouseViewBean.setCountry(warehouse.getCountryCode());
            warehouseViewBean.setCountryCode(warehouse.getCountryCode());
            warehouseViewBean.setLongitude(warehouse.getLongitude());
            warehouseViewBean.setLatitude(warehouse.getLatitude());
            
            DateFormat dateFormat = requestUtil.getCommonFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
            Date dateCreate = warehouse.getDateCreate();
            if (dateCreate != null) {
                warehouseViewBean.setDateCreate(dateFormat.format(dateCreate));
            } else {
                warehouseViewBean.setDateCreate(Constants.NOT_AVAILABLE);
            }

            Date dateUpdate = warehouse.getDateUpdate();
            if (dateUpdate != null) {
                warehouseViewBean.setDateUpdate(dateFormat.format(dateUpdate));
            } else {
                warehouseViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
            }

            warehouseViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.WAREHOUSE_DETAILS, requestData, warehouse));
            warehouseViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.WAREHOUSE_EDIT, requestData, warehouse));
        }
        return warehouseViewBean;
    }
    
    /**
     * 
     */
    public List<PaymentGatewayViewBean> buildListViewBeanPaymentGateway(final RequestData requestData, final List<AbstractPaymentGateway> paymentGateways) throws Exception {
        final List<PaymentGatewayViewBean> paymentGatewayViewBeans = new ArrayList<PaymentGatewayViewBean>();
        if (paymentGateways != null) {
            for (Iterator<AbstractPaymentGateway> iterator = paymentGateways.iterator(); iterator.hasNext();) {
                AbstractPaymentGateway paymentGateway = (AbstractPaymentGateway) iterator.next();
                paymentGatewayViewBeans.add(buildViewBeanPaymentGateway(requestData, paymentGateway));
            }
        }
        return paymentGatewayViewBeans;
    }

    /**
     * 
     */
    public PaymentGatewayViewBean buildViewBeanPaymentGateway(final RequestData requestData, final AbstractPaymentGateway paymentGateway) throws Exception {
        final MarketArea marketArea = requestData.getMarketArea();
        final PaymentGatewayViewBean paymentGatewayViewBean = new PaymentGatewayViewBean();
        if (paymentGateway != null) {
            paymentGatewayViewBean.setCode(paymentGateway.getCode());
            paymentGatewayViewBean.setName(paymentGateway.getName());
            paymentGatewayViewBean.setDescription(paymentGateway.getDescription());
            if(Hibernate.isInitialized(paymentGateway.getMarketAreas())
                    && paymentGateway.getMarketAreas() != null){
                paymentGatewayViewBean.setActive(paymentGateway.getMarketAreas().contains(marketArea));
            }

            List<PaymentGatewayAttribute> globalAttributes = paymentGateway.getGlobalAttributes();
            if(globalAttributes != null){
                for (Iterator<PaymentGatewayAttribute> iterator = globalAttributes.iterator(); iterator.hasNext();) {
                    PaymentGatewayAttribute attribute = (PaymentGatewayAttribute) iterator.next();
                    paymentGatewayViewBean.getGlobalAttributes().put(attribute.getAttributeDefinition().getCode(), attribute.getValueAsString());
                }
            }

            List<PaymentGatewayAttribute> marketAreaAttributes = paymentGateway.getMarketAreaAttributes(marketArea.getId());
            if(marketAreaAttributes != null){
                for (Iterator<PaymentGatewayAttribute> iterator = marketAreaAttributes.iterator(); iterator.hasNext();) {
                    PaymentGatewayAttribute attribute = (PaymentGatewayAttribute) iterator.next();
                    paymentGatewayViewBean.getMarketAreaAttributes().put(attribute.getAttributeDefinition().getCode(), attribute.getValueAsString());
                }
            }
            
            Set<PaymentGatewayOption> options = paymentGateway.getOptions();
            if(options != null){
                for (Iterator<PaymentGatewayOption> iterator = options.iterator(); iterator.hasNext();) {
                    PaymentGatewayOption option = (PaymentGatewayOption) iterator.next();
                    paymentGatewayViewBean.getOptions().put(option.getCode(), option.getOptionValue());
                }
            }
            
            DateFormat dateFormat = requestUtil.getCommonFormatDate(requestData, DateFormat.MEDIUM, DateFormat.MEDIUM);
            Date dateCreate = paymentGateway.getDateCreate();
            if (dateCreate != null) {
                paymentGatewayViewBean.setDateCreate(dateFormat.format(dateCreate));
            } else {
                paymentGatewayViewBean.setDateCreate(Constants.NOT_AVAILABLE);
            }

            Date dateUpdate = paymentGateway.getDateUpdate();
            if (dateUpdate != null) {
                paymentGatewayViewBean.setDateUpdate(dateFormat.format(dateUpdate));
            } else {
                paymentGatewayViewBean.setDateUpdate(Constants.NOT_AVAILABLE);
            }

            paymentGatewayViewBean.setDetailsUrl(backofficeUrlService.generateUrl(BoUrls.PAYMENT_GATEWAY_DETAILS, requestData, paymentGateway));
            paymentGatewayViewBean.setEditUrl(backofficeUrlService.generateUrl(BoUrls.PAYMENT_GATEWAY_EDIT, requestData, paymentGateway));

        }
        return paymentGatewayViewBean;
    }

    public List<AttributeValueViewBean> buildListViewBeanAttributeValue(final RequestData requestData, final List<AbstractAttribute> attributes) throws Exception {
        final List<AttributeValueViewBean> attributeValueViewBeans = new ArrayList<AttributeValueViewBean>();
        if (attributes != null) {
            for (Iterator<AbstractAttribute> iterator = attributes.iterator(); iterator.hasNext();) {
                AbstractAttribute attribute = (AbstractAttribute) iterator.next();
                attributeValueViewBeans.add(buildViewBeanAttributeValue(requestData, attribute));
            }
        }
        return attributeValueViewBeans;
    }
    
    /**
     * 
     */
    public List<AttributeDefinitionViewBean> buildListViewBeanAttributeDefinition(final RequestData requestData, final List<AttributeDefinition> attributeDefinitions) throws Exception {
        final List<AttributeDefinitionViewBean> attributeDefinitionViewBeans = new ArrayList<AttributeDefinitionViewBean>();
        if (attributeDefinitions != null) {
            for (Iterator<AttributeDefinition> iterator = attributeDefinitions.iterator(); iterator.hasNext();) {
                AttributeDefinition attributeDefinition = (AttributeDefinition) iterator.next();
                attributeDefinitionViewBeans.add(buildViewBeanAttributeDefinition(requestData, attributeDefinition));
            }
        }
        return attributeDefinitionViewBeans;
    }
    
    protected String getSpecificMessage(ScopeWebMessage scope, String key, Locale locale) {
        return getSpecificMessage(scope.getPropertyKey(), key, locale);
    }

    protected String getSpecificMessage(ScopeWebMessage scope, String key, Object[] params, Locale locale) {
        return getSpecificMessage(scope.getPropertyKey(), key, params, locale);
    }
    
    protected String getSpecificMessage(String scope, String key, Locale locale) {
        return coreMessageSource.getSpecificMessage(I18nKeyValueUniverse.BO.getPropertyKey(), scope, key, locale);
    }

    protected String getSpecificMessage(String scope, String key, Object[] params, Locale locale) {
        return coreMessageSource.getSpecificMessage(I18nKeyValueUniverse.BO.getPropertyKey(), scope, key, params, locale);
    }
    
}